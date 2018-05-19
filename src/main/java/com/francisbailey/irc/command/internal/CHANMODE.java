package com.francisbailey.irc.command.internal;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.exception.IRCActionException;
import com.francisbailey.irc.exception.InvalidModeOperationException;
import com.francisbailey.irc.exception.MissingModeArgumentException;
import com.francisbailey.irc.exception.ModeNotFoundException;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.message.ServerMessageBuilderWithReplyCode;
import com.francisbailey.irc.mode.*;
import com.francisbailey.irc.mode.strategy.ChannelModeStrategy;


import java.util.ArrayList;


/**
 * Created by fbailey on 07/05/17.
 */
public class CHANMODE implements Executable {


    /**
     * RFC 2812 - there is a maximum limit of three (3) changes per
     * command for modes that take a parameter.
     */
    private final int MAX_ARG_COUNT = 3;


    private class ChannelModeStrategyStruct {

        Mode mode;
        ChannelModeStrategy strategy;
        String arg;
        String operation;

        ChannelModeStrategyStruct(ChannelModeStrategy channelModeStrategy, Mode mode, String operation, String arg) {
            this.strategy = channelModeStrategy;
            this.mode = mode;
            this.operation = operation;
            this.arg = arg;
        }
    }


    /**
     * @param connection
     * @param clientMessage
     * @param server
     */
    @Override
    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String chanName = clientMessage.getParameter(0);
        Channel channel = server.getChannelManager().getChannel(chanName);
        String nick = connection.getClientInfo().getNick();

        if (channel == null) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
                .andMessage(nick + " " + chanName + " :No such channel, can't change mode")
                .build()
            );
        }
        else if (clientMessage.getParameterCount() < 2) {
            String modes = channel.getModes();

            if (modes.length() < 1) {
                connection.send(ServerMessageBuilder
                    .from(server.getName())
                    .withReplyCode(ServerMessage.ERR_NOCHANMODES)
                    .andMessage(nick)
                    .build()
                );
            }
            else {
                connection.send(ServerMessageBuilder
                    .from(server.getName())
                    .withReplyCode(ServerMessage.RPL_CHANNELMODEIS)
                    .andMessage(nick + " " + chanName + " +" + channel.getModes())
                    .build()
                );
            }
        }
        else {
            String modeAction = clientMessage.getParameter(1);
            String message = nick + " " + channel.getName();

            if (clientMessage.getParameterCount() >= 2) {

                if (!channel.hasModeForUser(connection, Mode.CHAN_OPERATOR) && !channel.hasModeForUser(connection, Mode.OWNER) && !connection.getModes().hasMode(Mode.OPERATOR)) {
                    connection.send(ServerMessageBuilder
                        .from(server.getName())
                        .withReplyCode(ServerMessage.ERR_CHANOPRIVSNEEDED)
                        .andMessage(nick + " " + chanName + " :Must be operator to change channel mode")
                        .build()
                    );
                }
                else {
                    String action = modeAction.substring(0, 1);

                    ServerMessageBuilderWithReplyCode serverMessage = ServerMessageBuilder.from(server.getName());

                    if (Mode.channelModes.containsKey(action)) {
                        this.listChannelMode(connection, channel, Mode.channelModes.get(action));
                    }
                    else {
                        try {
                            action = modeAction.substring(0, 1);
                            ArrayList<ChannelModeStrategyStruct> strategies;

                            if (!action.startsWith("+") && !action.startsWith("-")) {
                                throw new InvalidModeOperationException();
                            }

                            if (modeAction.length() > 2) {
                                strategies = joinedModeParseStrategy(server, clientMessage);
                            } else {
                                strategies = separatedModeParseStrategy(server, clientMessage);
                            }

                            for (ChannelModeStrategyStruct struct : strategies) {
                                if (struct.operation.equals("+")) {
                                    struct.strategy.addMode(channel, connection, struct.mode, struct.arg);
                                } else {
                                    struct.strategy.removeMode(channel, connection, struct.mode, struct.arg);
                                }
                            }

                            if (channel.getModes().length() > 0) {
                                connection.send(serverMessage
                                    .withReplyCode(ServerMessage.RPL_CHANNELMODEIS)
                                    .andMessage(message + " +" + channel.getModes())
                                    .build()
                                );
                            }
                        }
                        catch (MissingModeArgumentException e) {
                            connection.send(serverMessage
                                .withReplyCode(ServerMessage.ERR_NEEDMOREPARAMS)
                                .andMessage(nick + " :Missing mode argument")
                                .build()
                            );
                        }
                        catch (ModeNotFoundException e) {
                            connection.send(serverMessage
                                .withReplyCode( ServerMessage.ERR_UNKNOWNMODE)
                                .andMessage(nick + " " + e.getMessage() + " :Unknown mode")
                                .build()
                            );
                        }
                        catch (IRCActionException e) {
                            connection.send(serverMessage
                                .withReplyCode(e.getReplyCode())
                                .andMessage(e.getMessage())
                                .build()
                            );
                        }
                        catch (Exception e) {
                            connection.send(serverMessage
                                .withReplyCode(ServerMessage.ERR_NEEDMOREPARAMS)
                                .andMessage(nick + " :Unable to parse mode arguments")
                                .build()
                            );
                        }
                    }
                }
            }
        }
    }


    /**
     * Parse strategy for mode arguments in the form:
     * MODE #channel +ve <mask>
     * MODE #channel +be <mask1> <mask2>
     *
     * @param clientMessage - client message to parse mode from
     * @return a list of "strategies" for setting each mode
     */
    private ArrayList<ChannelModeStrategyStruct> joinedModeParseStrategy(ServerManager server, ClientMessage clientMessage)
            throws ModeNotFoundException, MissingModeArgumentException {
        int paramCount = clientMessage.getParameterCount();
        int modeArgIndex = 3;
        int argCount = 0;

        String operation = clientMessage.getParameter(2).substring(0, 1);

        String[] flags = clientMessage.getParameter(2).substring(1).split("");
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (String flag: flags) {

            Mode mode = Mode.channelModes.get(flag);
            ChannelModeStrategy strategy = server.getChannelModeStrategy(mode);

            if (mode == null || strategy == null) {
                 throw new ModeNotFoundException(flag);
            }

            if (mode.requiresArg() && argCount >= this.MAX_ARG_COUNT) {
                break;
            }

            String arg = null;

            if (mode.requiresArg() && argCount < this.MAX_ARG_COUNT) {
                if (modeArgIndex < paramCount) {
                    arg = clientMessage.getParameter(modeArgIndex);
                    modeArgIndex++;
                    argCount++;
                } else {
                    throw new MissingModeArgumentException();
                }
            }

            ChannelModeStrategyStruct struct = new ChannelModeStrategyStruct(strategy, mode, operation, arg);
            strategies.add(struct);
        }

        return strategies;
    }


    /**
     * Parse strategy for mode arguments in the form:
     * MODE #channel +b  <mask> +v
     * MODE #channel +b  <mask1> -e <mask2>
     * MODE #channel +O  <user>
     *
     * @param clientMessage
     * @return
     */
    private ArrayList<ChannelModeStrategyStruct> separatedModeParseStrategy(ServerManager server, ClientMessage clientMessage)
            throws ModeNotFoundException, MissingModeArgumentException, InvalidModeOperationException {

        int paramCount = clientMessage.getParameterCount();
        int argCount = 0;
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (int modeArgIndex = 1; modeArgIndex < paramCount; modeArgIndex++) {
            String modeAction = clientMessage.getParameter(modeArgIndex);

            if (modeAction.length() != 2) {
                throw new InvalidModeOperationException();
            }

            String arg = null;
            String operation = modeAction.substring(0, 1);
            String flag = modeAction.substring(1);

            Mode mode = Mode.channelModes.get(flag);
            ChannelModeStrategy strategy = server.getChannelModeStrategy(mode);

            if (mode == null || strategy == null) {
                throw new ModeNotFoundException(flag);
            }

            if (mode.requiresArg() && argCount >= this.MAX_ARG_COUNT) {
                break;
            }

            // arg required, look ahead to see if there
            // is one available
            if (mode.requiresArg()) {
                if ((modeArgIndex + 1) > paramCount) {
                    throw new MissingModeArgumentException();
                }

                argCount++;
                modeArgIndex++;
                arg = clientMessage.getParameter(modeArgIndex);
            }

            ChannelModeStrategyStruct struct = new ChannelModeStrategyStruct(strategy, mode, operation, arg);

            strategies.add(struct);
        }

        return strategies;
    }


    /**
     * We can list
     * O - owner
     * k - key
     * l - user limit
     * b - ban masks
     * I - invite masks
     * e - exception masks
     *
     * @TODO - to be implemented
     * @param connection
     * @param channel
     * @param mode
     */
    private void listChannelMode(Connection connection, Channel channel, Mode mode) {

    }


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public boolean canExecuteUnregistered() {
        return false;
    }
}

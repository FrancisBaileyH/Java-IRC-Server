package com.francisbailey.irc.command.internal;

import com.francisbailey.irc.*;
import com.francisbailey.irc.exception.IRCActionException;
import com.francisbailey.irc.exception.InvalidModeOperationException;
import com.francisbailey.irc.exception.MissingModeArgumentException;
import com.francisbailey.irc.exception.ModeNotFoundException;
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

        ChannelModeStrategyStruct(ChannelModeStrategy s, Mode m, String op, String arg) {
            this.strategy = s;
            this.mode = m;
            this.operation = op;
            this.arg = arg;
        }
    }


    /**
     * @param c
     * @param cm
     * @param instance
     */
    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String chanName = cm.getParameter(0);
        Channel channel = instance.getChannelManager().getChannel(chanName);
        String nick = c.getClientInfo().getNick();

        if (channel == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, nick + " " + chanName + " :No such channel, can't change mode"));
        }
        else if (cm.getParameterCount() < 2) {
            String modes = channel.getModes().toString();

            if (modes.length() < 1) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOCHANMODES, nick));
            }
            else {
                c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_CHANNELMODEIS, nick + " " + chanName + " +" + channel.getModes()));
            }
        }
        else {
            String modeAction = cm.getParameter(1);
            String message = nick + " " + channel.getName();

            if (cm.getParameterCount() >= 2) {

                if (!channel.hasModeForUser(c, Mode.CHAN_OPERATOR) && !channel.hasModeForUser(c, Mode.OWNER) && !c.getModes().hasMode(Mode.OPERATOR)) {
                    c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_CHANOPRIVSNEEDED, nick + " " + chanName + " :Must be operator to change channel mode"));
                }
                else {

                    String action = modeAction.substring(0, 1);

                    if (Mode.channelModes.containsKey(action)) {
                        this.listChannelMode(c, channel, Mode.channelModes.get(action));
                    }
                    else {
                        try {
                            action = modeAction.substring(0, 1);
                            ArrayList<ChannelModeStrategyStruct> strategies;

                            if (!action.startsWith("+") && !action.startsWith("-")) {
                                throw new InvalidModeOperationException();
                            }

                            if (modeAction.length() > 2) {
                                strategies = joinedModeParseStrategy(instance, cm);
                            } else {
                                strategies = separatedModeParseStrategy(instance, cm);
                            }

                            for (ChannelModeStrategyStruct struct : strategies) {
                                if (struct.operation.equals("+")) {
                                    struct.strategy.addMode(channel, c, struct.mode, struct.arg);
                                } else {
                                    struct.strategy.removeMode(channel, c, struct.mode, struct.arg);
                                }
                            }

                            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_CHANNELMODEIS, message + " +" + channel.getModes()));
                        } catch (MissingModeArgumentException e) {
                            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, nick + " :Missing mode argument"));
                        } catch (ModeNotFoundException e) {
                            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UNKNOWNMODE, nick + " " + e.getMessage() + " :Unknown mode"));
                        } catch (IRCActionException e) {
                            c.send(new ServerMessage(instance.getName(), e.getReplyCode(), e.getMessage()));
                        } catch (Exception e) {
                            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, nick + " :Unable to parse mode arguments"));
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
     * @param cm - client message to parse mode from
     * @return a list of "strategies" for setting each mode
     */
    private ArrayList<ChannelModeStrategyStruct> joinedModeParseStrategy(ServerManager instance, ClientMessage cm)
            throws ModeNotFoundException, MissingModeArgumentException, IllegalAccessException, InstantiationException {
        int paramCount = cm.getParameterCount();
        int modeArgIndex = 3;
        int argCount = 0;

        String operation = cm.getParameter(2).substring(0, 1);

        String[] flags = cm.getParameter(2).substring(1).split("");
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (String flag: flags) {

            Mode mode = Mode.channelModes.get(flag);
            ChannelModeStrategy strategy = instance.getChannelModeStrategy(mode);

            if (mode == null || strategy == null) {
                 throw new ModeNotFoundException(flag);
            }

            if (mode.requiresArg() && argCount >= this.MAX_ARG_COUNT) {
                break;
            }

            String arg = null;

            if (mode.requiresArg() && argCount < this.MAX_ARG_COUNT) {
                if (modeArgIndex < paramCount) {
                    arg = cm.getParameter(modeArgIndex);
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
     * @param cm
     * @return
     */
    private ArrayList<ChannelModeStrategyStruct> separatedModeParseStrategy(ServerManager instance, ClientMessage cm)
            throws ModeNotFoundException, MissingModeArgumentException, InvalidModeOperationException, IllegalAccessException, InstantiationException {

        int paramCount = cm.getParameterCount();
        int argCount = 0;
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (int modeArgIndex = 1; modeArgIndex < paramCount; modeArgIndex++) {
            String modeAction = cm.getParameter(modeArgIndex);

            if (modeAction.length() != 2) {
                throw new InvalidModeOperationException();
            }

            String arg = null;
            String operation = modeAction.substring(0, 1);
            String flag = modeAction.substring(1);

            Mode mode = Mode.channelModes.get(flag);
            ChannelModeStrategy strategy = instance.getChannelModeStrategy(mode);

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
                arg = cm.getParameter(modeArgIndex);
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
     * @param c
     * @param chan
     * @param mode
     */
    private void listChannelMode(Connection c, Channel chan, Mode mode) {

    }


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public Boolean canExecuteUnregistered() {
        return false;
    }
}

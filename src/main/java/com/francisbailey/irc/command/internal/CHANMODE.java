package com.francisbailey.irc.command.internal;

import com.francisbailey.irc.*;
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
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, chanName + " :No such channel, can't change mode"));
        }
        else if (cm.getParameterCount() < 2) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_CHANNELMODEIS, chanName + " +" + channel.getModes()));
        }
        else {
            this.handleChanMode(channel, c, cm, instance);
        }
    }


    /**
     * Mode arguments are complex and come in many forms. As a result, two different parse strategies must
     * be used to separate mode flags and their arguments.
     *
     * @param channel
     * @param c
     * @param cm
     * @param instance
     */
    private void handleChanMode(Channel channel, Connection c, ClientMessage cm, ServerManager instance) {

        String modeAction = cm.getParameter(1);
        String nick = c.getClientInfo().getNick();
        String message = nick + " " + channel.getName();

        if (cm.getParameterCount() >= 2) {

            if (!channel.hasModeForUser(c, ModeSet.CHAN_OPERATOR) && !channel.hasModeForUser(c, ModeSet.OWNER) && !c.getModes().hasMode(ModeSet.OPERATOR)) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOPRIVILEGES, nick + " :Must be operator to change channel mode"));
            }
            else {
                /**
                 * @TODO clause if we have just /MODE #channel i
                 * we should list the values
                 */

                try {
                    String action = modeAction.substring(0, 1);
                    ArrayList<ChannelModeStrategyStruct> strategies;

                    if (!action.startsWith("+") && !action.startsWith("-")) {
                        throw new InvalidModeOperationException();
                    }

                    if (modeAction.length() > 2) {
                        strategies = joinedModeParseStrategy(cm);
                    } else {
                        strategies = separatedModeParseStrategy(cm);
                    }

                    for (ChannelModeStrategyStruct struct: strategies) {
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
                    c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UNKNOWNMODE, e.getMessage() + " :Unknown mode"));
                } catch (Exception e) {
                    c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, nick  + " :Unable to parse mode arguments"));
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
    private ArrayList<ChannelModeStrategyStruct> joinedModeParseStrategy(ClientMessage cm)
            throws ModeNotFoundException, MissingModeArgumentException, IllegalAccessException, InstantiationException {
        int paramCount = cm.getParameterCount();
        int modeArgIndex = 3;

        String operation = cm.getParameter(2).substring(0, 1);

        String[] flags = cm.getParameter(2).substring(1).split("");
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (String flag: flags) {

            ChannelMode mode = ModeSet.chanModes.get(flag);

            if (mode == null) {
                 throw new ModeNotFoundException(flag);
            }

            String arg = null;

            if (mode.requiresArg()) {
                if (modeArgIndex < paramCount) {
                    arg = cm.getParameter(modeArgIndex);
                    modeArgIndex++;
                } else {
                    throw new MissingModeArgumentException();
                }
            }

            Class<? extends ChannelModeStrategy> strategy = mode.getStrategy();

            ChannelModeStrategy strategyInstance = strategy.newInstance();
            ChannelModeStrategyStruct struct = new ChannelModeStrategyStruct(strategyInstance, mode, operation, arg);
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
    private ArrayList<ChannelModeStrategyStruct> separatedModeParseStrategy(ClientMessage cm)
            throws ModeNotFoundException, MissingModeArgumentException, InvalidModeOperationException, IllegalAccessException, InstantiationException {

        int paramCount = cm.getParameterCount();
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (int modeArgIndex = 1; modeArgIndex < paramCount; modeArgIndex++) {
            String modeAction = cm.getParameter(modeArgIndex);

            if (modeAction.length() != 2) {
                throw new InvalidModeOperationException();
            }

            String arg = null;
            String operation = modeAction.substring(0, 1);
            String flag = modeAction.substring(1);

            ChannelMode mode = ModeSet.chanModes.get(flag);

            if (mode == null) {
                throw new ModeNotFoundException(flag);
            }

            // arg required, look ahead to see if there
            // is one available
            if (mode.requiresArg()) {
                if ((modeArgIndex + 1) > paramCount) {
                    throw new MissingModeArgumentException();
                }

                modeArgIndex++;
                arg = cm.getParameter(modeArgIndex);
            }

            Class<? extends ChannelModeStrategy> strategy = mode.getStrategy();

            ChannelModeStrategy strategyInstance = strategy.newInstance();
            ChannelModeStrategyStruct struct = new ChannelModeStrategyStruct(strategyInstance, mode, operation, arg);

            strategies.add(struct);
        }

        return strategies;
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

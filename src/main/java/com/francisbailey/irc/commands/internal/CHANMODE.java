package com.francisbailey.irc.commands.internal;

import com.francisbailey.irc.*;
import com.francisbailey.irc.modes.*;

import java.util.ArrayList;


/**
 * Created by fbailey on 07/05/17.
 */
public class CHANMODE implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String chanName = cm.getParameter(0);
        Channel channel = instance.getChannelManager().getChannel(chanName);

        if (channel == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, " :No such channel, can't change mode"));
        }
        else if (cm.getParameterCount() < 2) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_CHANNELMODEIS, " : +" + channel.getModes().toString()));
        }
        else {
            this.handleValidChanMode(channel, c, cm, instance);
        }
    }


    /**
     *
     * We can add a mode directly to the channel and we can add a mode directly to the channel user
     *
     * We have two operations per
     *
     * @param channel
     * @param c
     * @param cm
     * @param instance
     */
    private void handleValidChanMode(Channel channel, Connection c, ClientMessage cm, ServerManager instance) {

        String modeAction = cm.getParameter(1);

        if (cm.getParameterCount() > 2) {
            if (!channel.hasModeForUser(c, UserModes.LOCAL_OPERATOR.toString()) && !channel.hasModeForUser(c, UserModes.OPERATOR.toString())) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOPRIVILEGES, " :Must be operator to change channel modes"));
            }
            else {
                 String action = modeAction.substring(0, 1);

                              /**
                 * MODE #channel +v
                 * MODE #channel +ve <mask>
                 * MODE #channel +be <mask1> <mask2>
                 * MODE #channel +b  <mask> +v
                 * MODE #channel +b  <mask1> -e <mask2>
                 * MODE #channel +O  <user>
                 *
                 *
                 * if (modeAction.length > 2) {
                 *      parseStrategy1(ClientMessage);
                 * } else {
                 *      parseStrategy2(ClientMessage);
                 * }
                 *
                 *
                 */
                try {
                    if (!action.startsWith("+") && !action.startsWith("-")) {
                        throw new InvalidModeOperationException();
                    }

                    ArrayList<ChannelModeStrategyStruct> strategies = parseStrategy(cm);

                    for (ChannelModeStrategyStruct struct: strategies) {
                        if (action.equals("+")) {
                            struct.strategy.addMode(channel, c, struct.mode, struct.arg);
                        } else {
                            struct.strategy.removeMode(channel, c, struct.mode, struct.arg);
                        }
                    }
                } catch (MissingModeArgumentException e) {
                    c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, " :Missing mode argument"));
                } catch (ModeNotFoundException e) {
                    c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UNKNOWNMODE, " :Unknown mode"));
                } catch (Exception e) {
                    // Unknown error occurred, log it and send a user something back.
                }
            }
        }
    }


    /**
     * All together
     * It would probably be easier to return
     *
     * A mode and it's argument right?
     * Then we loop through and determine which Mode Strategy to use?
     * @param cm
     */
    private ArrayList<ChannelModeStrategyStruct> parseStrategy(ClientMessage cm)
            throws ModeNotFoundException, MissingModeArgumentException, IllegalAccessException, InstantiationException {
        int paramCount = cm.getParameterCount();
        int modeArgIndex = 3;

        String[] flags = cm.getParameter(2).substring(1).split("");
        ArrayList<ChannelModeStrategyStruct> strategies = new ArrayList<>();

        for (String flag: flags) {

            ChannelMode mode = ModeSet.chanModes.get(flag);

            if (mode == null) {
                 throw new ModeNotFoundException();
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
            ChannelModeStrategyStruct struct = new ChannelModeStrategyStruct();
            struct.arg = arg;
            struct.mode = mode;
            struct.strategy = strategyInstance;

            strategies.add(struct);
        }

        return strategies;
    }


    private void handleAddMode() {



    }


    private void handleRemoveMode() {

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

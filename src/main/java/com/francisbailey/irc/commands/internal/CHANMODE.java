package com.francisbailey.irc.commands.internal;

import com.francisbailey.irc.*;

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
//            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_CHANNELMODEIS, " : "));
        }
        else {
            this.handleValidChanMode(channel, c, cm, instance);
        }
    }


    private void handleValidChanMode(Channel channel, Connection c, ClientMessage cm, ServerManager instance) {

        ModeControl mc = instance.getModeControl();

        if (cm.getParameterCount() == 2) {

            if (!mc.targetHasMode("o", (ModeTarget)c, channel) || !mc.targetHasMode("O", (ModeTarget)c, channel)) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOPRIVILEGES, ":Must be operator to change channel modes"));
            }
            else {
                // set channel mode
            }
        }
        else {
            String mode = cm.getParameter(1);
        }
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

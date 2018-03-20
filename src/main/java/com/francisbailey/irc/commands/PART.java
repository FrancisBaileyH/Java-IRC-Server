package com.francisbailey.irc.commands;

import com.francisbailey.irc.*;

import java.util.ArrayList;

/**
 * Created by fbailey on 02/12/16.
 */
public class PART implements Executable {


    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String target = cm.getParameter(0);

        ChannelManager channelManager = instance.getChannelManager();

        // target is a channel, check that the channel exists
        if (channelManager.isChannel(target)) {

            if (instance.getChannelManager().getChannel(target) == null) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick()));
            }
            else {
                Channel chan = instance.getChannelManager().getChannel(target);

                if (!chan.hasUser(c)) {
                    c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOTONCHANNEL,  c.getClientInfo().getNick()));
                }
                else {
                    String message = cm.getParameterCount() > 1 ? cm.getParameter(1) : null;
                    this.partFromChannel(chan, c, message);
                }
            }
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }


    public synchronized void partFromChannel(Channel chan, Connection c, String message) {
        String m = chan.getName();

        if (message != null && !message.equals("")) {
            m += " :" + message;
        }

        String hostmask = c.getClientInfo().getHostmask();
        ServerMessage sm = new ServerMessage(hostmask, ServerMessage.RPL_PART, m);
        chan.broadcast(sm);
        chan.removeUser(c);
    }

}

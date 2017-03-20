package com.francisbailey.irc.commands;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 02/12/16.
 */
public class PART implements Executable {


    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String nick = c.getClientInfo().getNick();

        if (cm.getParameterCount() < 1) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, nick));
        }
        else {

            messageTarget(c, cm, instance);
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }


    private void messageTarget(Connection c, ClientMessage cm, ServerManager instance) {

        String target = cm.getParameter(0);

        // target is a channel, check that the channel exists
        if (target.startsWith("#")) {

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
                    chan.part(c, message);
                }
            }
        }
    }

}

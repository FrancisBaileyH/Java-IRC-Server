package com.francisbailey.commands;

import com.francisbailey.*;

/**
 * Created by fbailey on 04/12/16.
 */
public class WHO implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        if (cm.getParameterCount() < 1) {

            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, c.getClientInfo().getNick()));
        }
        else {

            Channel chan = instance.getChannelManager().getChannel(cm.getParameter(0));

            if (chan != null) {

                for (Connection user: chan.getUsers()) {

                    Client ci = user.getClientInfo();
                    String message = ci.getNick();
                    message +=  " " +  chan.getName();
                    message +=  " " + ci.getUsername();
                    message +=  " " +  ci.getHostname();
                    message +=  " " +  instance.getName();
                    message +=  " " +  ci.getNick();
                    message +=  " :0 " + ci.getRealname(); // @TODO change hop count to be dynamic
                    c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_WHOREPLY, message));
                }

                c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_ENDOFWHO));
            }
        }
    }
}

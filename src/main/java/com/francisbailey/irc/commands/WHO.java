package com.francisbailey.irc.commands;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 04/12/16.
 */
public class WHO implements Executable {


    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

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



    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }
}

package com.francisbailey.irc.commands;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 01/12/16.
 */
public class PING implements Executable {



    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String serverName = instance.getName();

       if (cm.getParameterCount() < 2) {
            c.send(new ServerMessage(serverName, ServerMessage.RPL_PONG, cm.getParameter(0)));
        }
        else {
            c.send(new ServerMessage(serverName, ServerMessage.ERR_NOSUCHSERVER, cm.getParameter(0) + " " + cm.getParameter(1)));
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }
}

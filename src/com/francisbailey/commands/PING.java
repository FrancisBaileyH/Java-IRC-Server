package com.francisbailey.commands;

import com.francisbailey.*;

/**
 * Created by fbailey on 01/12/16.
 */
public class PING implements Executable {


    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String serverName = instance.getName();

        if (cm.getParameterCount() < 1) {
            c.send(new ServerMessage(serverName, ServerMessage.ERR_NEEDMOREPARAMS));
        }
        else if (cm.getParameterCount() == 1) {
            c.send(new ServerMessage(serverName, ServerMessage.RPL_PONG, cm.getParameter(0)));
        }
        else {
            c.send(new ServerMessage(serverName, ServerMessage.ERR_NOSUCHSERVER, cm.getParameter(0) + " " + cm.getParameter(1)));
        }
    }
}

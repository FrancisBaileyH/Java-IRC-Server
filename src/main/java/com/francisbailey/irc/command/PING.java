package com.francisbailey.irc.command;

import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

/**
 * Created by fbailey on 01/12/16.
 */
public class PING implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String serverName = server.getName();

        if (clientMessage.getParameterCount() < 2) {
            connection.send(ServerMessageBuilder
                .from(serverName)
                .withReplyCode(ServerMessage.RPL_PONG)
                .andMessage(clientMessage.getParameter(0))
                .build()
            );
        }
        else {
            connection.send(ServerMessageBuilder
                .from(serverName)
                .withReplyCode(ServerMessage.ERR_NOSUCHSERVER)
                .andMessage(clientMessage.getParameter(0) + " " + clientMessage.getParameter(1))
                .build()
            );
        }
    }


    public int getMinimumParams() {
        return 1;
    }


    public Boolean canExecuteUnregistered() {
        return false;
    }
}

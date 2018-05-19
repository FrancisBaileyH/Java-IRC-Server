package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class QUIT implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        ArrayList<Channel> channels = server.getChannelManager().getChannelsByUser(connection);
        ArrayList<Connection> exclude = new ArrayList<>();
        exclude.add(connection);

        String message = "";

        if (clientMessage.getParameterCount() > 0) {
            message = clientMessage.getParameter(0);
        }

        for (Channel chan: channels) {
            PART command = new PART();
            command.partFromChannel(chan, connection, message);
        }

        connection.send(ServerMessageBuilder
            .from(connection.getClientInfo().getHostmask())
            .withReplyCode(ServerMessage.RPL_QUIT)
            .andMessage(":" + message)
            .build()
        );

        server.closeConnection(connection);
    }


    public int getMinimumParams() {
        return 0;
    }


    public Boolean canExecuteUnregistered() {
        return false;
    }
}

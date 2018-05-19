package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Client;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.mode.Mode;

/**
 * Created by fbailey on 04/12/16.
 */
public class WHO implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        Channel channel = server.getChannelManager().getChannel(clientMessage.getParameter(0));

        if (channel != null) {

            for (Connection user: channel.getUsers()) {

                if (!user.getModes().hasMode(Mode.INVISIBLE)) {
                    Client clientInfo = user.getClientInfo();
                    String message = clientInfo.getNick();
                    message += " " + channel.getName();
                    message += " " + clientInfo.getUsername();
                    message += " " + clientInfo.getHostname();
                    message += " " + server.getName();
                    message += " " + clientInfo.getNick();
                    message += " :0 " + clientInfo.getRealname(); // @TODO change hop count to be dynamic

                    connection.send(ServerMessageBuilder
                        .from(server.getName())
                        .withReplyCode(ServerMessage.RPL_WHOREPLY)
                        .andMessage(message)
                        .build()
                    );
                }
            }

            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.RPL_ENDOFWHO)
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

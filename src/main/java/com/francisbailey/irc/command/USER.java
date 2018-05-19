package com.francisbailey.irc.command;

import com.francisbailey.irc.Client;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

/**
 * Created by fbailey on 16/11/16.
 */
public class USER implements Executable {

    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String serverName = server.getName();

        if (connection.isRegistered()) {
            connection.send(ServerMessageBuilder
                .from(serverName)
                .withReplyCode(ServerMessage.ERR_ALREADYREGISTERED)
                .build()
            );
        }
        else {

            String nick = connection.getClientInfo().getNick();
            String username = clientMessage.getParameter(0);
            String hostName = connection.getHostNameInfo();
            String realName = clientMessage.getParameter(3);

            if (nick != null) {

                Client ci = new Client(nick, username, hostName, realName);
                server.registerConnection(connection, ci);

                this.sendRegistrationAcknowledgement(connection, server);
            }
        }
    }


    public int getMinimumParams() {
        return 4;
    }


    public boolean canExecuteUnregistered() {
        return true;
    }


    public void sendRegistrationAcknowledgement(Connection connection, ServerManager server) {

        String nick = connection.getClientInfo().getNick();
        String welcomeMessage = server.getConfig().getWelcomeMessage() + " " + connection.getClientInfo().getHostmask();
        String message = nick + " :" + welcomeMessage;
        String serverName = server.getName();


        connection.send(ServerMessageBuilder
            .from(serverName)
            .withReplyCode(ServerMessage.RPL_WELCOME)
            .andMessage(message)
            .build()
        );
    }
}

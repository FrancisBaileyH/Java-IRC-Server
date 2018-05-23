package com.francisbailey.irc.command;

import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

/**
 * Created by fbailey on 15/12/16.
 */
public class MOTD implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String motd = server.getConfig().getMotd();

        if (motd == null) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOMOTD)
                .andMessage(connection.getClientInfo().getNick())
                .build()
            );
        }
        else {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.RPL_MOTDSTART)
                .andMessage(":- Message of the day - ")
                .build()
            );

            String chunkedMessage = motd.replaceAll("(.{80})", "$1\n");

            for (String message: chunkedMessage.split("\\n")) {
                connection.send(ServerMessageBuilder
                    .from(server.getName())
                    .withReplyCode(ServerMessage.RPL_MOTD)
                    .andMessage(":- " + message)
                    .build()
                );
            }

            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.RPL_ENDOFMOTD)
                .andMessage(":End of the message of the day")
                .build()
            );
        }
    }


    public int getMinimumParams() {
        return 0;
    }


    public boolean canExecuteUnregistered() {
        return false;
    }
}

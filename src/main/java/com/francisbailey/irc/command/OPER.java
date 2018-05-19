package com.francisbailey.irc.command;

import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.command.internal.USERMODE;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.HashMap;

/**
 * Created by fbailey on 16/12/16.
 */
public class OPER implements Executable {


    @Override
    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String inputUsername = clientMessage.getParameter(0);
        String inputPassword = clientMessage.getParameter(1);


        HashMap<String, String> operators = server.getConfig().getOperators();

        String password = operators.get(inputUsername);

        if (password != null && password.equals(inputPassword)) {
            logger().debug("{} identified. Adding mode {}.", inputUsername, Mode.OPERATOR.getFlag());
            connection.getModes().addMode(Mode.OPERATOR);

            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.RPL_YOUREOP)
                .andMessage(connection.getClientInfo().getNick() + " :You are now an IRC operator")
                .build()
            );

            USERMODE m = new USERMODE();
            m.sendUsermode(connection, connection, server);
        }
        else {
            logger().info("Failed login attempt for: {}", inputUsername);
            connection.send(ServerMessageBuilder
                    .from(server.getName())
                    .withReplyCode(ServerMessage.ERR_PASSWDMISMATCH)
                    .andMessage(connection.getClientInfo().getNick() + " :Invalid username or password")
                    .build()
            );
        }
    }


    @Override
    public int getMinimumParams() {
        return 2;
    }


    @Override
    public boolean canExecuteUnregistered() {
        return false;
    }
}

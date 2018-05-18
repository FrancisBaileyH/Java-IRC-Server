package com.francisbailey.irc.command;

import com.francisbailey.irc.*;
import com.francisbailey.irc.command.internal.USERMODE;
import com.francisbailey.irc.mode.Mode;

import java.util.HashMap;

/**
 * Created by fbailey on 16/12/16.
 */
public class OPER implements Executable {


    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String inputUsername = cm.getParameter(0);
        String inputPassword = cm.getParameter(1);


        HashMap<String, String> operators = instance.getConfig().getOperators();

        String password = operators.get(inputUsername);

        if (password != null && password.equals(inputPassword)) {
            logger().debug("{} identified. Adding mode {}.", inputUsername, Mode.OPERATOR.getFlag());
            c.getModes().addMode(Mode.OPERATOR);
            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_YOUREOP, c.getClientInfo().getNick() + " :You are now an IRC operator"));
            USERMODE m = new USERMODE();
            m.sendUsermode(c, c, instance);

            return;
        }

        logger().info("Failed login attempt for: {}", inputUsername);
        c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_PASSWDMISMATCH, c.getClientInfo().getNick() + " :Invalid username or password"));
    }


    @Override
    public int getMinimumParams() {
        return 2;
    }


    @Override
    public Boolean canExecuteUnregistered() {
        return false;
    }
}

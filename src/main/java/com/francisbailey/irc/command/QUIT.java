package com.francisbailey.irc.command;

import com.francisbailey.irc.*;

import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class QUIT implements Executable {


    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        ArrayList<Channel> channels = instance.getChannelManager().getChannelsByUser(c);
        ArrayList<Connection> exclude = new ArrayList<>();
        exclude.add(c);

        String message = "";

        if (cm.getParameterCount() > 0) {
            message = cm.getParameter(0);
        }

        for (Channel chan: channels) {
            PART command = new PART();
            command.partFromChannel(chan, c, message);
        }

        c.send(new ServerMessage(c.getClientInfo().getHostmask(), ServerMessage.RPL_QUIT, ":" + message));

        instance.closeConnection(c);
    }



    public int getMinimumParams() {
        return 0;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }
}

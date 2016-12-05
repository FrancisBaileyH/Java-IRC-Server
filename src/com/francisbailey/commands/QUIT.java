package com.francisbailey.commands;

import com.francisbailey.*;

import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class QUIT implements Executable {


    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        ArrayList<Channel> channels = instance.getChannelManager().getChannelsByUser(c);
        ArrayList<Connection> exclude = new ArrayList<>();
        exclude.add(c);

        String message = "";

        if (cm.getParameterCount() > 0) {
            message = cm.getParameter(0);
        }

        for (Channel chan: channels) {

            chan.part(c, message);
        }

        c.send(new ServerMessage(c.getClientInfo().getHostmask(), ServerMessage.RPL_QUIT, ":" + message));

        instance.closeConnection(c);
    }
}

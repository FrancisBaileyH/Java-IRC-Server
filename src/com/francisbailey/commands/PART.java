package com.francisbailey.commands;

import com.francisbailey.*;

import java.util.ArrayList;

/**
 * Created by fbailey on 02/12/16.
 */
public class PART implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String nick = c.getClientInfo().getNick();

        if (cm.getParameterCount() < 1) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, nick));
        }
        else {

            messageTarget(c, cm, instance);
        }
    }


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public Boolean canExecuteUnregistered() {
        return false;
    }


    private void messageTarget(Connection c, ClientMessage cm, ServerManager instance) {

        String target = cm.getParameter(0);

        // target is a channel, check that the channel exists
        if (target.startsWith("#")) {

            if (instance.getChannelManager().getChannel(target) == null) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick()));
            }
            else {
                Channel chan = instance.getChannelManager().getChannel(target);

                String message = cm.getParameterCount() > 1 ? cm.getParameter(1) : null;
                chan.part(c, message);
            }
        }
    }

}

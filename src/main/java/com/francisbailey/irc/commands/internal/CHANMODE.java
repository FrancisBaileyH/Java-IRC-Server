package com.francisbailey.irc.commands.internal;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 07/05/17.
 */
public class CHANMODE implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String chanName = cm.getParameter(0);
        Channel target = instance.getChannelManager().getChannel(chanName);
        Modes usersModes = c.getModes();

        if (target == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, " :No such channel, can't change mode"));
        }
        else if (!usersModes.hasMode(target, "o") || !usersModes.hasMode(target, "O")) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOPRIVILEGES, ":Must be operator to change channel modes"));
        }
        else {

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
}

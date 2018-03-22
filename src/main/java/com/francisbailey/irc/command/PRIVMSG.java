package com.francisbailey.irc.command;

import com.francisbailey.irc.*;

import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class PRIVMSG implements Executable {



    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String target = cm.getParameter(0);
        String message = cm.getParameter(1);

        // target is a channel, check that the channel exists
        if (instance.getChannelManager().isChannel(target)) {

            Channel chan = instance.getChannelManager().getChannel(target);
            sendChannelMessage(c, chan, target, message);
        }
        else if (instance.getChannelManager().isChannelType(target)){
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick()));
        }
        else {
            Connection targetCon = instance.findConnectionByNick(target);
            sendPrivateMessage(c, targetCon, instance, message);
        }

    }



    public int getMinimumParams() {
        return 2;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }


    private void sendChannelMessage(Connection c, Channel chan, String target, String message) {

        if (chan.hasUser(c)) {

            ArrayList<Connection> excluded = new ArrayList<>();
            excluded.add(c);
            ServerMessage sm = new ServerMessage(c.getClientInfo().getHostmask(), ServerMessage.RPL_PRIVMSG, target + " :" + message);
            chan.broadcast(sm, excluded);
        }
        else {
            ServerMessage sm = new ServerMessage(c.getClientInfo().getHostmask(), ServerMessage.ERR_NOTONCHANNEL, c.getClientInfo().getNick() + " :not on channel");
            c.send(sm);
        }
    }


    private void sendPrivateMessage(Connection c, Connection targetCon, ServerManager instance, String message) {

        if (targetCon == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHNICK, c.getClientInfo().getNick()));
        } else {
            ServerMessage sm = new ServerMessage(c.getClientInfo().getHostmask(), ServerMessage.RPL_PRIVMSG, targetCon.getClientInfo().getNick() + " :" + message);
            targetCon.send(sm);
        }
    }
}

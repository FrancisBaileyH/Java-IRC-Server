package com.francisbailey.commands;

import com.francisbailey.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class PRIVMSG implements Executable {


    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String nick = c.getClientInfo().getNick();

        if (cm.getParameterCount() < 2) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NEEDMOREPARAMS, nick));
        }
        else {
            messageTarget(cm.getParameter(0), cm.getParameter(1), c, instance);
        }
    }


    /**
     * Attempt to send the privmsg to the specified target
     * @param message
     * @param target
     * @param c
     * @param instance
     */
    private void messageTarget(String target, String message, Connection c, ServerManager instance) {

         // target is a channel, check that the channel exists
        if (instance.getChannelManager().isChannel(target)) {

            Channel chan = instance.getChannelManager().getChannel(target);
            sendChannelMessage(c, chan, target, message);
        }
        else if (target.startsWith("#")){
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick()));
        }
        else {
            Connection targetCon = instance.findConnectionByNick(target);
            sendPrivateMessage(c, targetCon, instance, message);
        }
    }


    private void sendChannelMessage(Connection c, Channel chan, String target, String message) {

        if (chan.hasUser(c)) {

            ArrayList<Connection> excluded = new ArrayList<>();
            excluded.add(c);
            ServerMessage sm = new ServerMessage(c.getClientInfo().getHostmask(), ServerMessage.RPL_PRIVMSG, target + " :" + message);
            chan.broadcast(sm, excluded);
        }
        else {
            //error
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

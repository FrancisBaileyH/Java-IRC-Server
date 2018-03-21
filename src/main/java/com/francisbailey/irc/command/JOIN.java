package com.francisbailey.irc.command;

import com.francisbailey.irc.*;
import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class JOIN implements Executable {


    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String channel = cm.getParameter(0);
        ChannelManager channelManager = instance.getChannelManager();
        String nick = c.getClientInfo().getNick();
        String message;
        String replyCode;

        if (!channelManager.hasChannel(channel)) {
            message = nick + " " + channel + " :No such channel";
            replyCode = ServerMessage.ERR_NOSUCHCHANNEL;
            c.send(new ServerMessage(instance.getName(), replyCode, message));
        }
        else {
            Channel chan = channelManager.getChannel(channel);
            ArrayList<Connection> channelUsers = chan.getUsers();

            if (channelUsers.indexOf(c) <= 0) {
                chan.addUser(c);

                String hostmask = c.getClientInfo().getHostmask();
                ServerMessage sm = new ServerMessage(hostmask, ServerMessage.RPL_JOIN, chan.getName());
                chan.broadcast(sm);
            }

            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_TOPIC, nick + " " + channel + " :" + chan.getTopic()));
            this.sendChannelUsers(c, chan, instance.getName());
        }
    }


    public int getMinimumParams() {
        return 1;
    }


    public Boolean canExecuteUnregistered() {
        return false;
    }


    /**
     * After a user successfully joins a channel they must be sent a list of
     * all users currently in the channel
     * @param c
     * @param chan
     * @param serverName
     */
    private void sendChannelUsers(Connection c, Channel chan, String serverName) {

        ArrayList<String> nicks = chan.getNicks();
        String chanName = chan.getName();
        String nick = c.getClientInfo().getNick();

        for (String chanNick: nicks) {
            c.send(new ServerMessage(serverName, ServerMessage.RPL_NAMREPLY, nick + " = " + chanName + " :" + chanNick));
        }

        c.send(new ServerMessage(serverName, ServerMessage.RPL_ENDOFNAMES, nick + " " + chanName + " :End of NAMES list"));
    }
}

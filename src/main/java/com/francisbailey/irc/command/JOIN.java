package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.ChannelManager;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 */
public class JOIN implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String channel = clientMessage.getParameter(0);
        ChannelManager channelManager = server.getChannelManager();
        String nick = connection.getClientInfo().getNick();

        if (!channelManager.hasChannel(channel)) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
                .andMessage(nick + " " + channel + " :No such channel")
                .build()
            );
        }
        else {
            Channel chan = channelManager.getChannel(channel);
            ArrayList<Connection> channelUsers = chan.getUsers();

            if (channelUsers.indexOf(connection) <= 0) {
                chan.addUser(connection);

                String hostmask = connection.getClientInfo().getHostmask();
                chan.broadcast(ServerMessageBuilder
                    .from(hostmask)
                    .withReplyCode(ServerMessage.RPL_JOIN)
                    .andMessage(chan.getName())
                    .build()
                );
            }

            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.RPL_TOPIC)
                .andMessage(nick + " " + channel + " :" + chan.getTopic())
                .build()
            );
            this.sendChannelUsers(connection, chan, clientMessage.getCommandOrigin());
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
     *
     * @param connection - The client that joined the channel
     * @param channel - The channel the client joined
     * @param origin - The origin of the message
     */
    private void sendChannelUsers(Connection connection, Channel channel, String origin) {

        ArrayList<String> nicks = channel.getNicks();
        String chanName = channel.getName();
        String nick = connection.getClientInfo().getNick();

        for (String chanNick: nicks) {
            connection.send(ServerMessageBuilder.from(origin)
                .withReplyCode(ServerMessage.RPL_NAMREPLY)
                .andMessage(nick + " = " + chanName + " :" + chanNick)
                .build()
            );
        }

        connection.send(ServerMessageBuilder.from(origin)
            .withReplyCode(ServerMessage.RPL_ENDOFNAMES)
            .andMessage(nick + " " + chanName + " :End of NAMES list")
            .build()
        );
    }
}

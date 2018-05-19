package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.ChannelManager;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

/**
 * Created by fbailey on 02/12/16.
 */
public class PART implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String target = clientMessage.getParameter(0);

        ChannelManager channelManager = server.getChannelManager();
        Channel channel = channelManager.getChannel(target);

        if (channel == null) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
                .andMessage(connection.getClientInfo().getNick())
                .build()
            );
        }
        else {
             if (!channel.hasUser(connection)) {
                connection.send(ServerMessageBuilder
                    .from(server.getName())
                    .withReplyCode(ServerMessage.ERR_NOTONCHANNEL)
                    .andMessage(connection.getClientInfo().getNick())
                    .build()
                );
            }
            else {
                String message = clientMessage.getParameterCount() > 1 ? clientMessage.getParameter(1) : null;
                this.partFromChannel(channel, connection, message);
            }
        }
    }


    public int getMinimumParams() {
        return 1;
    }


    public boolean canExecuteUnregistered() {
        return false;
    }


    public synchronized void partFromChannel(Channel channel, Connection connection, String partMessage) {
        String message = channel.getName();

        if (partMessage != null && !partMessage.equals("")) {
            message += " :" + partMessage;
        }

        channel.broadcast(ServerMessageBuilder
            .from(connection.getClientInfo().getHostmask())
            .withReplyCode(ServerMessage.RPL_PART)
            .andMessage(message)
            .build()
        );
        channel.removeUser(connection);
    }

}

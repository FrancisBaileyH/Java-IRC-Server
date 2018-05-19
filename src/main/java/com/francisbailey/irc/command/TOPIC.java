package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

public class TOPIC implements Executable {


    /**
     * RFC 2812 - 3.2.4
     *
     * TOPIC <channel> [ <topic> ]
     *
     * The topic for channel <channel> is returned if there is no <topic>
     * given.  If the <topic> parameter is present, the topic for that
     * channel will be changed, if this action is allowed for the user
     * requesting it.  If the <topic> parameter is an empty string, the
     * topic for that channel will be removed.
     *
     * RFC2811 - 4.2.6
     *
     * If a topic is marked as "secret" or "private" TOPIC commands
     * must be ignored.
     *
     * RFC 2811 - 4.2.8
     *
     * Channel topic is only settable by the Channel Operator or Owner
     * if the "t" flag is present on the channel.
     *
     *
     * @param connection
     * @param clientMessage
     * @param server
     */
    @Override
    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String target = clientMessage.getParameter(0);
        Channel channel = server.getChannelManager().getChannel(target);

        if (channel == null) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
                .build()
            );
        }
        else if (!channel.hasUser(connection)) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOTONCHANNEL)
                .build()
            );
        }
        else {
            if (channel.hasMode(Mode.SECRET) || channel.hasMode(Mode.PRIVATE)) {
                logger().info("Channel: {} is marked as secret/private. Ignoring TOPIC command", channel.getName());
                return;
            }

            if (clientMessage.getParameterCount() < 2) {
                this.sendTopic(server.getName(), connection, channel);
            }
            else {
                ModeSet userModes = connection.getModes();

                boolean canSetTopic = userModes.hasMode(Mode.OPERATOR) || userModes.hasMode(Mode.LOCAL_OPERATOR);
                canSetTopic = canSetTopic || channel.hasModeForUser(connection, Mode.CHAN_OPERATOR) || channel.hasModeForUser(connection, Mode.OWNER);

                if (!canSetTopic && channel.hasMode(Mode.OP_TOPIC_ONLY)) {
                    logger().info("User: {} tried to set topic on: {} with insufficient permissions",
                        connection.getClientInfo().getHostmask(),
                        channel.getName()
                    );

                    connection.send(ServerMessageBuilder
                        .from(server.getName())
                        .withReplyCode(ServerMessage.ERR_CHANOPRIVSNEEDED)
                        .andMessage(channel.getName())
                        .build()
                    );
                }
                else {
                    String topic = clientMessage.getParameter(1);
                    channel.setTopic(topic);

                    for (Connection user: channel.getUsers()) {
                        this.sendTopic(connection.getClientInfo().getHostmask(), user, channel);
                    }
                }
            }
        }
    }


    /**
     * RFC2812 - 5.1
     * If no topic is present a RPL_NOTOPIC must be sent
     *
     *
     * @param origin
     * @param connection
     * @param channel
     */
    public void sendTopic(String origin, Connection connection, Channel channel) {

        String topic = channel.getTopic();
        ServerMessage message;

        if (topic == null || topic.equals("")) {
            message = ServerMessageBuilder
                    .from(origin)
                    .withReplyCode(ServerMessage.RPL_NOTOPIC)
                    .andMessage(channel.getName() + " :No topic is set")
                    .build();
        }
        else {
            message = ServerMessageBuilder
                    .from(origin)
                    .withReplyCode(ServerMessage.RPL_TOPIC)
                    .andMessage(channel.getName() + " :" + topic)
                    .build();
        }

        connection.send(message);
    }


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public boolean canExecuteUnregistered() {
        return false;
    }
}

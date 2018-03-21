package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.mode.Mode;


/**
 * Created by fbailey on 19/03/18.
 */
public class StandardChannelModeStrategy implements ChannelModeStrategy {

    @Override
    public void addMode(Channel channel, Connection c, Mode m, String arg) {
        channel.addMode(m);
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode m, String arg) {
        channel.removeMode(m);
    }
}

package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

/**
 * Created by fbailey on 22/03/18.
 */
public class ChannelModeArgStrategy implements ChannelModeStrategy {

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) {
        if (mode.equals(ModeSet.CHAN_KEY)) {
            channel.setKey(arg);
        } else if (mode.equals(ModeSet.USER_LIMIT)) {

        }
    }


    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) {

    }
}

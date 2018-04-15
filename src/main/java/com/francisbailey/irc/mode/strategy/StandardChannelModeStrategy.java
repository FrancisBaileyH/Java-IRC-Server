package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.mode.Mode;


/**
 * Created by fbailey on 19/03/18.
 */
public class StandardChannelModeStrategy extends AbstractModeStrategy implements ChannelModeStrategy {


    public StandardChannelModeStrategy(ServerManager instance) {
        super(instance);
    }

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) {
        channel.addMode(mode);
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) {
        channel.removeMode(mode);
    }
}

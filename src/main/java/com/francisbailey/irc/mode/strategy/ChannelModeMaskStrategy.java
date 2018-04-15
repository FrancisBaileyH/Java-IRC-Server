package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.mode.Mode;

/**
 * Created by fbailey on 22/03/18.
 */
public class ChannelModeMaskStrategy extends AbstractModeStrategy implements ChannelModeStrategy {

    public ChannelModeMaskStrategy(ServerManager instance) {
        super(instance);
    }

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) {
        channel.addMask(mode, arg);
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) {
        channel.removeMask(mode, arg);
    }
}

package com.francisbailey.irc.modes;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;


/**
 * Created by fbailey on 19/03/18.
 */
public interface ChannelModeStrategy {
    public void addMode(Channel channel, Connection c, Mode mode, String arg);
    public void removeMode(Channel channel, Connection c, Mode mode, String arg);
}

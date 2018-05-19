package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.exception.IRCActionException;
import com.francisbailey.irc.mode.Mode;


/**
 * Created by fbailey on 19/03/18.
 */
public interface ChannelModeStrategy {
    public void addMode(Channel channel, Connection c, Mode mode, String arg) throws IRCActionException;
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) throws IRCActionException;
}

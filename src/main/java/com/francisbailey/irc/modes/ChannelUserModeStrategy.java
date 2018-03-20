package com.francisbailey.irc.modes;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;

import java.util.ArrayList;

/**
 * Created by fbailey on 19/03/18.
 */
public class ChannelUserModeStrategy implements ChannelModeStrategy {


    @Override
    public void addMode(Channel channel, Connection c, Mode m, String arg) {

        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            // send error
        }

        channel.addModeForUser(target, mode);

    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode m, String arg) {

    }
}

package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

/**
 * Created by fbailey on 19/03/18.
 */
public class ChannelUserModeStrategy implements ChannelModeStrategy {


    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) {
        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            // send error
//            c.send(new ServerMessage())
        } else {
            channel.addModeForUser(target, mode);
        }
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) {
        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            // send error
        } else if (target.getModes().hasMode(ModeSet.OWNER) && !c.equals(target) && !c.getModes().hasMode(ModeSet.OPERATOR)) {
            // send error
        } else {
            channel.removeModeForUser(target, mode);
        }
    }
}

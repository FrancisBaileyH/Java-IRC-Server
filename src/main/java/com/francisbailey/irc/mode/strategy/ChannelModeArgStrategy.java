package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.exception.ChannelKeyIsSetException;
import com.francisbailey.irc.exception.IRCActionException;
import com.francisbailey.irc.mode.Mode;


/**
 * Created by fbailey on 22/03/18.
 */
public class ChannelModeArgStrategy extends AbstractModeStrategy implements ChannelModeStrategy {

    public ChannelModeArgStrategy(ServerManager instance) {
        super(instance);
    }

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) throws IRCActionException {
        if (mode.equals(Mode.CHAN_KEY)) {
            try {
                channel.setKey(arg);
            } catch (ChannelKeyIsSetException e) {
                throw new IRCActionException(ServerMessage.ERR_KEYSET, c.getClientInfo().getNick() + " " + channel.getName() + " :Error key already set");
            }
        } else if (mode.equals(Mode.USER_LIMIT)) {
            try {
                Integer limit = Integer.parseInt(arg);

                if (limit < 1) {
                    throw new Exception("Bad user limit value");
                }
                channel.setUserLimit(limit);
            } catch (Exception e) {
                throw new IRCActionException(ServerMessage.ERR_BADMASK, c.getClientInfo().getNick() + " " + channel.getName() + " :Invalid user limit");
            }
        }
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) throws IRCActionException {
        if (mode.equals(Mode.CHAN_KEY)) {
            channel.clearKey();
        } else {
            throw new IRCActionException(ServerMessage.ERR_BADMASK, c.getClientInfo().getNick() + " " + channel.getName() + " :Invalid user limit");
        }
    }
}

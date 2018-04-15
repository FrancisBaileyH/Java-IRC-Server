package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.ServerMessage;
import com.francisbailey.irc.exception.IRCActionException;
import com.francisbailey.irc.mode.Mode;

/**
 * Created by fbailey on 19/03/18.
 */
public class ChannelUserModeStrategy extends AbstractModeStrategy implements ChannelModeStrategy {


    public ChannelUserModeStrategy(ServerManager instance) {
        super(instance);
    }

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) throws IRCActionException {
        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            throw new IRCActionException(ServerMessage.ERR_USERNOTINCHANNEL, c.getClientInfo().getNick() + " :User not in channel");
        } else {
            channel.addModeForUser(target, mode);
        }
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) throws IRCActionException {
        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            throw new IRCActionException(ServerMessage.ERR_USERNOTINCHANNEL, c.getClientInfo().getNick() + " :User not in channel");
        } else if (channel.hasModeForUser(target, Mode.OWNER) && !c.equals(target) && !c.getModes().hasMode(Mode.OPERATOR)) {
            throw new IRCActionException(ServerMessage.ERR_NOPRIVILEGES, c.getClientInfo().getNick() + " :Can't change owner's modes");
        } else {
            channel.removeModeForUser(target, mode);
        }
    }
}

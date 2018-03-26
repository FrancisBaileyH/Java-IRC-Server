package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

/**
 * Created by fbailey on 19/03/18.
 */
public class ChannelUserModeStrategy extends AbstractModeStrategy implements ChannelModeStrategy {


    public ChannelUserModeStrategy(ServerManager instance) {
        super(instance);
    }

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) {
        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOTONCHANNEL, c.getClientInfo().getNick() + " :User not in channel"));
        } else {
            channel.addModeForUser(target, mode);
        }
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) {
        Connection target = channel.findConnectionByNick(arg);

        if (target == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOTONCHANNEL, c.getClientInfo().getNick() + " :User not in channel"));
        } else if (target.getModes().hasMode(Mode.OWNER) && !c.equals(target) && !c.getModes().hasMode(Mode.OPERATOR)) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOPRIVILEGES, c.getClientInfo().getNick() + " :Can't change owner's modes"));
        } else {
            channel.removeModeForUser(target, mode);
        }
    }
}

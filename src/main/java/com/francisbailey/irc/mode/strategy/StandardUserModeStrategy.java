package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Connection;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

/**
 * Created by fbailey on 25/03/18.
 */
public class StandardUserModeStrategy extends AbstractModeStrategy implements UserModeStrategy {

    public StandardUserModeStrategy(ServerManager instance) {
        super(instance);
    }

    @Override
    public void addMode(Connection c, Connection target, Mode mode) {
        if (!mode.equals(Mode.OPERATOR) && !mode.equals(Mode.LOCAL_OPERATOR) && !mode.equals(Mode.AWAY)) {
            c.getModes().addMode(mode);
        }
    }

    @Override
    public void removeMode(Connection c, Connection target, Mode mode) {
        ModeSet originModes = c.getModes();

        if (!mode.equals(Mode.RESTRICTED) || originModes.hasMode(Mode.OPERATOR) || originModes.hasMode(Mode.LOCAL_OPERATOR)) {
            c.getModes().removeMode(mode);
        }
    }
}

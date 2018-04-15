package com.francisbailey.irc.mode.strategy;


import com.francisbailey.irc.Connection;
import com.francisbailey.irc.mode.Mode;

/**
 * Created by fbailey on 25/03/18.
 */
public interface UserModeStrategy {

    public void addMode(Connection c, Connection target, Mode mode);
    public void removeMode(Connection c, Connection target, Mode mode);
}

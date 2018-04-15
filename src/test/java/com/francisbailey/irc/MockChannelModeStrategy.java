package com.francisbailey.irc;

import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.strategy.ChannelModeStrategy;

/**
 * Created by fbailey on 27/03/18.
 */
public class MockChannelModeStrategy implements ChannelModeStrategy {

    private Mode lastMode = null;
    private String lastArg = null;
    private OPERATION lastOp = null;

    public enum OPERATION {
        ADD,
        REMOVE
    }

    @Override
    public void addMode(Channel channel, Connection c, Mode mode, String arg) {
        this.lastMode = mode;
        this.lastArg = arg;
        this.lastOp = OPERATION.ADD;
    }

    @Override
    public void removeMode(Channel channel, Connection c, Mode mode, String arg) {
        this.lastMode = mode;
        this.lastArg = arg;
        this.lastOp = OPERATION.REMOVE;
    }

    public Mode getLastModeSet() {
        return this.lastMode;
    }


    public String getLastArg() {
        return this.lastArg;
    }


    public OPERATION getLastOp() {
        return this.lastOp;
    }
}

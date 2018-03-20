package com.francisbailey.irc.modes;

/**
 * Created by fbailey on 19/03/18.
 */
public class ChannelMode extends Mode {

    private Class<? extends ChannelModeStrategy> strategy;

    public ChannelMode(String flag, String name, boolean requiresArg, Class<? extends ChannelModeStrategy> strategy) {
        super(flag, name, requiresArg);
        this.strategy = strategy;
    }

    public Class<? extends ChannelModeStrategy> getStrategy() {
        return this.strategy;
    }
}

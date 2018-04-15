package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.ServerManager;

/**
 * Created by fbailey on 25/03/18.
 */
public class AbstractModeStrategy {


    protected ServerManager instance;


    public AbstractModeStrategy(ServerManager instance) {
        this.instance = instance;
    }

}

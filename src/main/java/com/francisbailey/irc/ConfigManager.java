package com.francisbailey.irc;

/**
 * Created by fbailey on 16/11/16.
 */
public class ConfigManager {

    private Config config;


    public ConfigManager(Config c) {
        this.config = c;
    }

    public Config getConfig() {
        return this.config;
    }

}

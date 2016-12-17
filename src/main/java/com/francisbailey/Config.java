package com.francisbailey;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by fbailey on 16/11/16.
 */
public class Config {

    public ArrayList<String> commands;
    public final int maxNickLen = 9;
    public final int maxMessageLen = 510;

    public String serverName;
    public String welcomeMessage;
    public String motd;
    public List<HierarchicalConfiguration> channels;
    public List<HierarchicalConfiguration> operators;


    private HierarchicalConfiguration config;


    public Config(HierarchicalConfiguration config) {

        this.config = config;
        this.serverName = config.getString("name");
        this.welcomeMessage = config.getString("welcome-message");
        this.motd = config.getString("motd");
        this.channels = config.configurationsAt("channels.channel");
        this.operators = config.configurationsAt("operators.operator");
    }


    public String find(String key) {

        return config.getString(key);
    }

}

package com.francisbailey.irc;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by fbailey on 16/11/16.
 */
public class Config {

    public ArrayList<String> commands;
    public final int maxNickLen = 9;
    public final int maxMessageLen = 510;

    public String serverName;
    public String networkName;
    public String welcomeMessage;
    public String motd;
    public List<HierarchicalConfiguration> channels;
    public List<HierarchicalConfiguration> operators;
    public HashMap<String, String[]> defaultModes;


    private HierarchicalConfiguration config;


    public Config(HierarchicalConfiguration config) {

        this.config = config;
        this.serverName = config.getString("name");
        this.welcomeMessage = config.getString("welcome-message");
        this.motd = config.getString("motd");
        this.channels = config.configurationsAt("channels.channel");
        this.operators = config.configurationsAt("operators.operator");
        this.networkName = config.getString("network-name");
        this.defaultModes = new HashMap<>();

        this.defaultModes.put("server-user", new String[] {
           "i", "a", "w", "r", "o", "O", "s"
        });

        this.defaultModes.put("server-channel", new String[] {
            "a", "i", "m", "n", "q", "p", "s", "r" ,"t" ,"k", "l", "b", "e"
        });

        this.defaultModes.put("channel-user", new String[] {
           "o", "O", "v"
        });

    }


    public String find(String key) {

        return config.getString(key);
    }

}

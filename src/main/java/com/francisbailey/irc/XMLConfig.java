package com.francisbailey.irc;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLConfig implements Config {

    public ArrayList<String> commands;
    public final int maxNickLen = 9;
    public final int maxMessageLen = 510;


    public final String serverName;
    public final String networkName;
    public final String welcomeMessage;
    public final String motd;
    public final List<HierarchicalConfiguration> channels;
    public final HashMap<String, String> operators;


    private HierarchicalConfiguration config;


    public XMLConfig(HierarchicalConfiguration config) {

        this.config = config;
        this.serverName = config.getString("name");
        this.welcomeMessage = config.getString("welcome-message");
        this.motd = config.getString("motd");
        this.channels = config.configurationsAt("channels.channel");
        List<HierarchicalConfiguration> operatorConfigs = config.configurationsAt("operators.operator");
        this.operators = new HashMap<>();
        this.networkName = config.getString("network-name");

        for (HierarchicalConfiguration operator: operatorConfigs) {
            String username = operator.getString("username");
            String password = operator.getString("password");

            this.operators.put(username, password);
        }

    }

    public final int getMaxNickLen() {
        return maxNickLen;
    }

    public final int getMaxMessageLen() {
        return maxMessageLen;
    }

    public final String getServerName() {
        return serverName;
    }

    public final String getNetworkName() {
        return networkName;
    }

    public final String getWelcomeMessage() {
        return welcomeMessage;
    }

    public final String getMotd() {
        return motd;
    }

    public final List<HierarchicalConfiguration> getChannels() {
        return channels;
    }

    public final HashMap<String, String> getOperators() {
        return (HashMap)operators.clone();
    }
}

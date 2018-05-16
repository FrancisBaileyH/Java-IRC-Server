package com.francisbailey.irc;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.List;

public class MockConfig implements Config {


    private HashMap<String, String> operators;


    public MockConfig() {
        this.operators = new HashMap<>();
    }


    public void setOperators(HashMap<String,String> operators) {
        this.operators = operators;
    }

    @Override
    public int getMaxNickLen() {
        return 0;
    }

    @Override
    public int getMaxMessageLen() {
        return 0;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public String getNetworkName() {
        return null;
    }

    @Override
    public String getWelcomeMessage() {
        return null;
    }

    @Override
    public String getMotd() {
        return null;
    }

    @Override
    public List<HierarchicalConfiguration> getChannels() {
        return null;
    }

    @Override
    public HashMap<String, String> getOperators() {
        return this.operators;
    }
}

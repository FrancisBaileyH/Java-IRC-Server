package com.francisbailey.irc;


import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fbailey on 16/11/16.
 */
public interface Config {
    public int getMaxNickLen();
    public int getMaxMessageLen();
    public String getServerName();
    public String getNetworkName();
    public String getWelcomeMessage();
    public  String getMotd();
    public List<HierarchicalConfiguration> getChannels();
    public HashMap<String, String> getOperators();
}

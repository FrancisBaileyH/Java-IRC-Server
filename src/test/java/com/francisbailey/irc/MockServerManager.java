package com.francisbailey.irc;


import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.strategy.ChannelModeStrategy;
import com.francisbailey.irc.mode.strategy.UserModeStrategy;

import java.util.ArrayList;

/**
 * Created by fbailey on 05/05/17.
 */
public class MockServerManager implements ServerManager {


    private Config config;
    private ChannelManager cm;
    private MockChannelModeStrategy cms;
    private String name;
    public ArrayList<Connection> connections;
    public ArrayList<Connection> registeredConnections;

    private Connection findNickValue;


    public MockServerManager(String name, ChannelManager cm) {
        this.name = name;
        this.cm = cm;
        this.connections = new ArrayList<>();
        this.registeredConnections = new ArrayList<>();
        this.config = new MockConfig();
        this.findNickValue = null;
        this.cms = new MockChannelModeStrategy();
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void closeConnection(Connection c) {

    }

    @Override
    public void registerConnection(Connection c, Client clientInfo) {

    }

    @Override
    public Connection findConnectionByNick(String nick) {
        return this.findNickValue;
    }

    public void setFindNickValue(Connection c) {
        this.findNickValue = c;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public ChannelManager getChannelManager() {
        return this.cm;
    }


    @Override
    public void broadcast(ServerMessage sm) {

    }

    @Override
    public ChannelModeStrategy getChannelModeStrategy(Mode mode) {
        return this.cms;
    }

    @Override
    public UserModeStrategy getUserModeStrategy(Mode mode) {
        return null;
    }

    @Override
    public boolean isChannelMode(String flag) {
        return false;
    }

    @Override
    public boolean isUserMode(String flag) {
        return false;
    }
}

package com.francisbailey;


import com.francisbailey.irc.*;

import java.util.ArrayList;

/**
 * Created by fbailey on 05/05/17.
 */
public class MockServerManager implements ServerManager {


    private ChannelManager cm;
    private String name;
    public ArrayList<Connection> connections;
    public ArrayList<Connection> registeredConnections;


    public MockServerManager(String name, ChannelManager cm) {
        this.name = name;
        this.cm = cm;
        this.connections = new ArrayList<>();
        this.registeredConnections = new ArrayList<>();
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
        return null;
    }

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public ChannelManager getChannelManager() {
        return this.cm;
    }

    @Override
    public void broadcast(ServerMessage sm) {

    }
}

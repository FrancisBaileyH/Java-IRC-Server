package com.francisbailey.irc;

/**
 * Created by fbailey on 16/11/16.
 */
public interface ServerManager extends ModeResource {

    public String getName();
    public void closeConnection(Connection c);
    public void registerConnection(Connection c, Client clientInfo);
    public Connection findConnectionByNick(String nick);
    public Config getConfig();
    public ChannelManager getChannelManager();
    public void broadcast(ServerMessage sm);
    public ModeControl getModeControl();
}

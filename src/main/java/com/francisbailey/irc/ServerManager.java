package com.francisbailey.irc;

import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.strategy.ChannelModeStrategy;
import com.francisbailey.irc.mode.strategy.UserModeStrategy;

/**
 * Created by fbailey on 16/11/16.
 */
public interface ServerManager {

    public String getName();
    public void closeConnection(Connection c);
    public void registerConnection(Connection c, Client clientInfo);
    public Connection findConnectionByNick(String nick);
    public Config getConfig();
    public ChannelManager getChannelManager();
    public void broadcast(ServerMessage sm);
    public ChannelModeStrategy getChannelModeStrategy(Mode mode);
    public UserModeStrategy getUserModeStrategy(Mode mode);
    public boolean isChannelMode(String flag);
    public boolean isUserMode(String flag);
}

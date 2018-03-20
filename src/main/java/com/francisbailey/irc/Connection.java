package com.francisbailey.irc;

import com.francisbailey.irc.modes.ModeSet;

/**
 * Created by fbailey on 05/05/17.
 */
public interface Connection {
    public void terminate();
    public void send(SendableMessage message);
    public void register(Client clientInfo);
    public Boolean isRegistered();
    public Client getClientInfo();
    public void setClientInfo(Client c);
    public String getHostNameInfo();
    public ModeSet getModes();
    public void setModes(ModeSet ms);
}

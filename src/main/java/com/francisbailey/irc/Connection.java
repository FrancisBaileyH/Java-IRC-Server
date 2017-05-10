package com.francisbailey.irc;

/**
 * Created by fbailey on 05/05/17.
 */
public interface Connection extends ModeSettable {
    public void terminate();
    public void send(SendableMessage message);
    public void register(Client clientInfo);
    public Boolean isRegistered();
    public Client getClientInfo();
    public void setClientInfo(Client c);
    public String getHostNameInfo();
}

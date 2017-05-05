package com.francisbailey;

import com.francisbailey.irc.Client;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.SendableMessage;

/**
 * Created by fbailey on 05/05/17.
 */
public class MockConnection implements Connection {

    private String lastOutput;
    private Client clientInfo;
    private Boolean registered;

    public String getLastOutput() {
        return this.lastOutput;
    }

    @Override
    public void terminate() {

    }

    @Override
    public void send(SendableMessage message) {
        this.lastOutput = message.compile();
    }

    public void register(Client clientInfo) {
        this.clientInfo = clientInfo;
        this.registered = true;
    }

    public Boolean isRegistered() {
        return this.registered;
    }

    public Client getClientInfo() {
        return this.clientInfo;
    }

    public void setClientInfo(Client c) {
        this.clientInfo = c;
    }

    public String getHostNameInfo() {
        return "";
    }
}

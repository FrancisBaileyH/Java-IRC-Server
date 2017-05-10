package com.francisbailey;

import com.francisbailey.irc.Client;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.SendableMessage;


import java.util.ArrayList;

/**
 * Created by fbailey on 05/05/17.
 */
class MockConnection implements Connection {

    private ArrayList<SendableMessage> outputBuffer;
    private Client clientInfo;
    private Boolean registered;


    MockConnection() {
        this.outputBuffer = new ArrayList<>();
    }


    String getLastOutput() {

        int size = this.outputBuffer.size();

        if (size > 0) {
            return this.outputBuffer.get(size - 1).compile();
        }

        return null;
    }

    public String getAllOutput() {
        String output = "";

        for (SendableMessage m: this.outputBuffer) {
            output += m.compile();
        }

        return output;
    }

    @Override
    public void terminate() {

    }

    @Override
    public void send(SendableMessage message) {
        this.outputBuffer.add(message);
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

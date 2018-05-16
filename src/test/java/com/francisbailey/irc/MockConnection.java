package com.francisbailey.irc;

import com.francisbailey.irc.mode.ModeSet;


import java.util.ArrayList;

/**
 * Created by fbailey on 05/05/17.
 */
public class MockConnection implements Connection {

    private ArrayList<SendableMessage> outputBuffer;
    private Client clientInfo;
    private Boolean registered;
    private ModeSet modes;


    public MockConnection() {
        this.outputBuffer = new ArrayList<>();
        this.modes = new ModeSet();
    }


    public ServerMessage getLastOutput() {

        int size = this.outputBuffer.size();

        if (size > 0) {
            return (ServerMessage)this.outputBuffer.get(size - 1);
        }

        return null;
    }

    public ArrayList<ServerMessage> getAllOutput() {

        ArrayList<ServerMessage> messages = new ArrayList<>();

        for (SendableMessage message: this.outputBuffer) {
            messages.add((ServerMessage)message);
        }

        return messages;
    }


    public void clearOutputBuffer() {
        this.outputBuffer.clear();
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

    @Override
    public ModeSet getModes() {
        return this.modes;
    }

    public void setModes(ModeSet ms) {
        this.modes = ms;
    }
}

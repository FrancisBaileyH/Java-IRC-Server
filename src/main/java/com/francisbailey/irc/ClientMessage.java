package com.francisbailey.irc;

import java.util.ArrayList;

/**
 * Created by fbailey on 02/11/16.
 */

/**
 * Incoming IRC messages are parsed and a ClientMessage is created
 * for internal consumption.
 */
public class ClientMessage {

    private String message;
    private String commandOrigin;
    private String command;
    private ArrayList<String> parameters;


    public ClientMessage(String command, String message) {
        this(command, message, new ArrayList<String>());
    }


    public ClientMessage(String command, String message, ArrayList<String> parameters) {
        this(command, message, parameters, "");
    }


    public ClientMessage(String command, String message, ArrayList<String> parameters, String commandOrigin) {
        this.command = command;
        this.message = message;
        this.parameters = parameters;
        this.commandOrigin = commandOrigin;
    }

    public String getCommand() {
        return this.command;
    }

    public String getMessage() { return this.message; }

    public ArrayList<String> getParameters() {
        return this.parameters;
    }

    public String getParameter(int i) {
        return this.parameters.get(i);
    }

    public int getParameterCount() {
        return this.parameters.size();
    }

    public String getCommandOrigin() {
        return this.commandOrigin;
    }

}

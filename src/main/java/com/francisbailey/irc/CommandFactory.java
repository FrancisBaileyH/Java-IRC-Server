package com.francisbailey.irc;


import com.francisbailey.irc.exception.InvalidCommandException;
import com.francisbailey.irc.message.ClientMessage;

/**
 * Created by fbailey on 16/11/16.
 */
public class CommandFactory {


    public Executable build(ClientMessage msg) throws InvalidCommandException {

        Executable exe;

        try {
            Class<?> c = Class.forName("com.francisbailey.irc.command." + msg.getCommand());
            exe = (Executable) c.getConstructor().newInstance();
        }
        catch (Exception e) {
            throw new InvalidCommandException(msg.getMessage());
        }

        return exe;
    }

}

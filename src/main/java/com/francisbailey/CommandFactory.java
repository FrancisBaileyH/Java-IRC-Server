package com.francisbailey;


/**
 * Created by fbailey on 16/11/16.
 */
public class CommandFactory {


    public Executable build(ClientMessage msg) throws InvalidCommandException {

        Executable exe;

        try {
            Class<?> c = Class.forName("com.francisbailey.commands." + msg.getCommand());
            exe = (Executable) c.getConstructor().newInstance();
        }
        catch (Exception e) {
            throw new InvalidCommandException(msg.getMessage());
        }

        return exe;
    }

}

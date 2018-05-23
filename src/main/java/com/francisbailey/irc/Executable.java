package com.francisbailey.irc;

import com.francisbailey.irc.message.ClientMessage;

/**
 * Created by fbailey on 04/11/16.
 */
public interface Executable extends Loggable {

    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server);
    public int getMinimumParams();
    public boolean canExecuteUnregistered();
}

package com.francisbailey.irc.command;


import com.francisbailey.irc.ChannelManager;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.command.internal.CHANMODE;
import com.francisbailey.irc.command.internal.USERMODE;
import com.francisbailey.irc.message.ClientMessage;

/**
 * Created by fbailey on 13/12/16.
 */
public class MODE implements Executable {


    /**
     * The mode command target can be a channel or a user,
     * so we'll decide what the target is and defer the
     * execution to our internal mode command: CHANMODE or USERMODE
     * @param connection
     * @param clientMessage
     * @param server
     */
    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String target = clientMessage.getParameter(0);
        ChannelManager channelManager = server.getChannelManager();

        if (channelManager.isChannelType(target)) {
            Executable exe = new CHANMODE();
            exe.execute(connection, clientMessage, server);
        }
        else {
            Executable exe = new USERMODE();
            exe.execute(connection, clientMessage, server);
        }
    }


    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }
}

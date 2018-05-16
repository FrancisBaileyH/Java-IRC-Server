package com.francisbailey.irc.command;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 15/12/16.
 */
public class MOTD implements Executable {


    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String motd = instance.getConfig().getMotd();

        if (motd == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOMOTD, c.getClientInfo().getNick()));
        }
        else {
            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_MOTDSTART, ":- Message of the day - "));
            String chunkedMessage = motd.replaceAll("(.{80})", "$1\n");

            for (String message: chunkedMessage.split("\\n")) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_MOTD, ":- " + message));
            }

            c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_ENDOFMOTD, ":End of the message of the day"));
        }
    }


    public int getMinimumParams() {
        return 0;
    }


    public Boolean canExecuteUnregistered() {
        return false;
    }
}

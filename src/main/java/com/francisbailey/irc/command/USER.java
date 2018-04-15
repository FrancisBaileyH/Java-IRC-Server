package com.francisbailey.irc.command;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 16/11/16.
 */
public class USER implements Executable {

    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String serverName = instance.getName();

        if (c.isRegistered()) {
            c.send(new ServerMessage(serverName, ServerMessage.ERR_ALREADYREGISTERED));
        }
        else {

            String nick = c.getClientInfo().getNick();
            String username = cm.getParameter(0);
            String hostName = c.getHostNameInfo();
            String realName = cm.getParameter(3);

            if (nick != null) {

                Client ci = new Client(nick, username, hostName, realName);
                instance.registerConnection(c, ci);

                this.sendRegistrationAcknowledgement(c, instance);
            }
        }
    }


    public int getMinimumParams() {
        return 4;
    }


    public Boolean canExecuteUnregistered() {
        return true;
    }


    public void sendRegistrationAcknowledgement(Connection c, ServerManager instance) {

        String nick = c.getClientInfo().getNick();
        String welcomeMessage = instance.getConfig().welcomeMessage + " " + c.getClientInfo().getHostmask();
        String message = nick + " :" + welcomeMessage;
        String serverName = instance.getName();


        c.send(new ServerMessage(serverName, ServerMessage.RPL_WELCOME, message));
    }
}

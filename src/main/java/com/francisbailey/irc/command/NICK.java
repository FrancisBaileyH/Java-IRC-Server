package com.francisbailey.irc.command;

import com.francisbailey.irc.*;

import java.util.ArrayList;


/**
 * Created by fbailey on 16/11/16.
 *
 * @TODO generate new unique id in place of nickname. That way,
 * at connection registration if nick is taken unique id takes its place
 * and then client can automatically set new nick if need be.
 */
public class NICK implements Executable {


    private ServerManager instance;
    private Connection c;
    private ClientMessage cm;



    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        this.c = c;
        this.cm = cm;
        this.instance = instance;

        String serverName = instance.getName();

        String nick = cm.getParameter(0);
        String replyCode = nickIsValid(nick);

        if (replyCode != null) {

            String message = "";

            if (c.isRegistered()) {
                message = c.getClientInfo().getNick();
            }
            else {
                message = "*";
            }

            message = message + " " + nick + " :Nickname already in use";

            c.send(new ServerMessage(serverName, replyCode, message));
        }
        else {
            setNickChange(nick);
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return true;
    }


    /**
     * Handle the nick change, including seeing if the nickname
     * change needs to be broadcasted.
     * @param nick
     */
    private void setNickChange(String nick) {

        String oldName = c.getClientInfo().getHostmask();
        c.getClientInfo().setNick(nick);

        if (c.isRegistered()) {

            ArrayList<Channel> channels = instance.getChannelManager().getChannelsByUser(c);
            ArrayList<Connection> exclude = new ArrayList<>();
            exclude.add(c);

            for (Channel chan: channels) {
                chan.broadcast(new ServerMessage(oldName, ServerMessage.RPL_NICK, nick), exclude);
            }

            c.send(new ServerMessage(oldName, ServerMessage.RPL_NICK, nick));
        }
    }


    /**
     * Check if a nickname is valid and return
     * a server reply code if it's not.
     *
     * @param nick
     * @return
     */
    private String nickIsValid(String nick) {

        if (nick.length() > 9) {
            return ServerMessage.ERR_ERRONEOUSNICKNAME;
        }
        else if (instance.findConnectionByNick(nick) != null) {
            return ServerMessage.ERR_NICKNAMEINUSE;
        }

        return null;
    }

}
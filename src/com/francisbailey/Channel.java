package com.francisbailey;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by fbailey on 01/12/16.
 */
public class Channel {


    private String topic;
    private String name;
    private ArrayList<Connection> users;


    public Channel(String name, String topic) {

        this.name = name;
        this.topic = topic;
        this.users = new ArrayList<>();
    }


    /**
     * Broadcast a JOIN message when a user joins the channel
     * @param c
     */
    public synchronized void join(Connection c) {

        if (this.users.indexOf(c) <= 0) {

            this.users.add(c);

            String hostmask = c.getClientInfo().getHostmask();
            ServerMessage sm = new ServerMessage(hostmask, ServerMessage.RPL_JOIN, this.name);
            this.broadcast(sm);
        }
    }

    /**
     * @return
     */
    public String getTopic() {
        return this.topic;
    }


    public String getName() {
        return this.name;
    }

    /**
     * Broadcast a part message when a user leaves the channel
     * @param c
     */
    public synchronized void part(Connection c, String message) {

        String m = this.name;

        if (message != null && !message.equals("")) {
            m += " :" + message;
        }

        if (this.users.contains(c)) {
            String hostmask = c.getClientInfo().getHostmask();
            ServerMessage sm = new ServerMessage(hostmask, ServerMessage.RPL_PART, m);
            this.broadcast(sm);
            this.users.remove(c);
        }
    }


    /**
     * Get all user nicks names currently in the channel
     * @return
     */
    public synchronized ArrayList<String> getNicks() {

        ArrayList<String> nicks = new ArrayList<>();

        for (Connection con: this.users) {
            nicks.add(con.getClientInfo().getNick());
        }

        return nicks;
    }


    /**
     * Get all users connected to channel
     * @return
     */
    public synchronized ArrayList<Connection> getUsers() {

        return this.users;
    }


    /**
     * Check for the existence of a user in a channel
     * @param c
     * @return
     */
    public synchronized Boolean hasUser(Connection c) {

        return this.users.contains(c);
    }


    /**
     * Send a message to all users on the channel
     * @param sm
     */
    public synchronized void broadcast(ServerMessage sm) {

        this.broadcast(sm, null);
    }


    public synchronized void broadcast(ServerMessage sm, ArrayList<Connection> exclude) {

        for (Connection c: this.users) {

            if (exclude == null || !exclude.contains(c)) {
                c.send(sm);
            }
        }
    }

}

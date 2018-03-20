package com.francisbailey.irc;

import com.francisbailey.irc.modes.Mode;
import com.francisbailey.irc.modes.ModeSet;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @TODO MaxUserLimit dynamically set from config
 * Created by fbailey on 01/12/16.
 */
public class Channel {

    private String topic;
    private String name;

    private int userLimit;
    private String key;
    private String banMask;
    private String invitationMask;
    private String banExceptionMask;

    private ArrayList<Connection> users;
    private ModeSet modes;
    private HashMap<Connection, ModeSet> channelUserModes;


    public Channel(String name, String topic) {

        this.name = name;
        this.topic = topic;
        this.users = new ArrayList<>();
        this.modes = new ModeSet();
        this.channelUserModes = new HashMap<>();
    }

    /**
     *
     * @param mode
     */
    public synchronized boolean addMode(Mode mode) {
        return this.modes.addMode(mode);
    }

    /**
     *
     */
    public synchronized String getModes() {
        return this.modes.getModes();
    }

    /**
     *
     * @param mode
     * @return
     */
    public synchronized boolean removeMode(Mode mode) {
        return this.modes.removeMode(mode);
    }


    /**
     *
     * @param c
     */
    public synchronized void addUser(Connection c) {
        if (!this.users.contains(c)) {
            this.users.add(c);
        }
    }


    /**
     *
     * @param c
     */
    public synchronized void removeUser(Connection c) {
        if (this.users.contains(c)) {
            this.users.remove(c);
        }
    }


    /**
     * @TODO should we throw exception?
     * @param c
     * @param mode
     */
    public synchronized void addModeForUser(Connection c, Mode mode) {

        if (this.hasUser(c)) {

            ModeSet ms;

            if (this.channelUserModes.containsKey(c)) {
                ms = this.channelUserModes.get(c);
            } else {
                ms = new ModeSet();
            }

            ms.addMode(mode);
            this.channelUserModes.put(c, ms);
        }
    }


    /**
     * @TODO should we throw an exception?
     * @param c
     * @param mode
     */
    public synchronized void removeModeForUser(Connection c, Mode mode) {
        if (this.channelUserModes.containsKey(c)) {

            ModeSet ms = this.channelUserModes.get(c);
            ms.removeMode(mode);
            this.channelUserModes.put(c, ms);
        }
    }


    public synchronized boolean hasModeForUser(Connection c, Mode mode) {
        if (this.channelUserModes.containsKey(c)) {
            ModeSet ms = this.channelUserModes.get(c);

            return ms.hasMode(mode);
        }

        return false;
    }


    /**
     *
     * @param c
     * @return
     */
    public synchronized ModeSet getModesForUser(Connection c) {
        return this.channelUserModes.get(c);
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

        return (ArrayList<Connection>)this.users.clone();
    }


    public synchronized boolean hasUser(String nick) {

        for (Connection user: this.users) {
            if (user.getClientInfo().getNick().equals(nick)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Check for the existence of a user in a channel
     * @param c
     * @return
     */
    public synchronized boolean hasUser(Connection c) {

        return this.users.contains(c);
    }


    /**
     *
     * @param nick
     * @return
     */
    public synchronized Connection findConnectionByNick(String nick) {

        for (Connection user: this.users) {
            if (user.getClientInfo().getNick().equals(nick)) {
                return user;
            }
        }

        return null;
    }


    public String getTopic() {
        return this.topic;
    }


    public String getName() {
        return this.name;
    }


    /**
     * Send a message to all users on the channel
     * @param sm
     */
    public synchronized void broadcast(ServerMessage sm) {

        this.broadcast(sm, null);
    }


    /**
     * Broadcast message to all users on channel except
     * those in the exclusion list.
     * @param sm
     * @param exclude
     */
    public synchronized void broadcast(ServerMessage sm, ArrayList<Connection> exclude) {

        for (Connection c: this.users) {

            if (exclude == null || !exclude.contains(c)) {
                c.send(sm);
            }
        }
    }

    public synchronized void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }


    public synchronized void setKey(String key) {
        this.key = key;
    }

    /**
     * @TODO - just a placeholder. A channel can have multiple
     * Ban Masks, so we'll have to come up with a proper strategy
     * for managing them.
     * @param banMask
     */
    public synchronized void setBanMask(String banMask) {
        this.banMask = banMask;
    }

    public synchronized void setInvitationMask(String invitationMask) {
        this.invitationMask = invitationMask;
    }

    public synchronized void setBanExceptionMask(String banExceptionMask) {
        this.banExceptionMask = banExceptionMask;
    }

    public synchronized String getBanMask() {
        return banMask;
    }

    public synchronized String getKey() {
        return key;
    }

    public synchronized int getUserLimit() {
        return userLimit;
    }

    public synchronized String getInvitationMask() {
        return invitationMask;
    }

    public synchronized String getBanExceptionMask() {
        return banExceptionMask;
    }
}

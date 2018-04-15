package com.francisbailey.irc;

import com.francisbailey.irc.exception.ChannelKeyIsSetException;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;


/**
 * @TODO MaxUserLimit dynamically set from config
 * Created by fbailey on 01/12/16.
 */
public class Channel {

    private String topic;
    private String name;

    private int userLimit;
    private String key;

    private HashMap<Mode,ArrayList<Pattern>> masks;
    private ArrayList<Connection> users;
    private ModeSet modes;
    private HashMap<Connection, ModeSet> channelUserModes;


    public Channel(String name, String topic) {

        this.name = name;
        this.topic = topic;
        this.users = new ArrayList<>();
        this.modes = new ModeSet();
        this.channelUserModes = new HashMap<>();

        this.masks = new HashMap<>();
    }

    /**
     * Add a mode to a channel if it doesn't already have the given mode
     * @param mode
     */
    public synchronized boolean addMode(Mode mode) {
        return this.modes.addMode(mode);
    }

    /**
     *
     * @return
     */
    public synchronized String getModes() {
        return this.modes.getModes();
    }

    /**
     * Remove a mode if the channel has one
     *
     * @param mode
     * @return
     */
    public synchronized boolean removeMode(Mode mode) {
        return this.modes.removeMode(mode);
    }


    /**
     * Clear all modes
     */
    public synchronized void clearModes() {
        this.modes.clearModes();
    }


    /**
     * Verify that the channel has a given mode
     * @param mode
     * @return
     */
    public synchronized boolean hasMode(Mode mode) {
        return this.modes.hasMode(mode);
    }


    /**
     * Add a user to the channel
     * @param c
     */
    public synchronized void addUser(Connection c) {
        if (!this.users.contains(c)) {
            this.users.add(c);
        }
    }


    /**
     * Remove a user from the channel
     * @param c
     */
    public synchronized void removeUser(Connection c) {
        if (this.users.contains(c)) {
            this.channelUserModes.remove(c);
            this.users.remove(c);
        }
    }


    /**
     * Add a mode for a given user
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
     * Remove a mode for a given channel user
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


    /**
     * Check if a channel user has a given mode
     * @param c
     * @param mode
     * @return
     */
    public synchronized boolean hasModeForUser(Connection c, Mode mode) {
        if (this.channelUserModes.containsKey(c)) {
            ModeSet ms = this.channelUserModes.get(c);

            return ms.hasMode(mode);
        }

        return false;
    }


    /**
     * Get all channel modes for a user
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


    /**
     * Pattern is a final class, so it's equals() method cannot
     * be overridden. As a result we can not use the default
     * list.contains or list.remove on a Pattern object.
     * @param mode
     * @param searchMask
     * @return
     */
    public boolean hasMask(Mode mode, String searchMask) {

        ArrayList<Pattern> masks = this.masks.get(mode);

        if (mode == null) {
            return false;
        }

        for (Pattern mask: masks) {
            if (mask.pattern().equals(searchMask)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the channel topic
     * @return
     */
    public String getTopic() {
        return this.topic;
    }


    /**
     * Get the channel name
     * @return
     */
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


    /**
     * Add a mask for a given mode
     * @param mode
     * @param mask
     */
    public synchronized void addMask(Mode mode, String mask) {

        ArrayList<Pattern> masks = this.masks.get(mode);
        Pattern maskPattern = Pattern.compile(mask);

        if (masks == null) {
            masks = new ArrayList<>();
            masks.add(maskPattern);
        } else if (!masks.contains(maskPattern)) {
            masks.add(maskPattern);
        }

        this.masks.put(mode, masks);
    }


    /**
     * Get all masks for a given mode
     * @param mode
     * @return
     */
    public synchronized ListIterator<Pattern> getMask(Mode mode) {
        ArrayList<Pattern> masks = this.masks.get(mode);

        if (masks != null && masks.size() > 0) {
            return masks.listIterator();
        }

        return null;
    }


    /**
     * Remove a mask if one exists for the given mode
     * @param mode
     * @param searchMask
     */
    public synchronized void removeMask(Mode mode, String searchMask) {
        ArrayList<Pattern> masks = this.masks.get(mode);

        if (mode == null) {
            return;
        }

        for (int i = 0; i < masks.size(); i++) {
            Pattern mask = masks.get(i);
            if (mask.pattern().equals(searchMask)) {
                masks.remove(i);
                return;
            }
        }
    }


    /**
     * Clear all masks for a given mode
     * @param mode
     */
    public synchronized void clearMasks(Mode mode) {
        ArrayList<Pattern> masks = this.masks.get(mode);

        if (masks != null) {
            masks.clear();
        }
    }


    public synchronized void setKey(String key) throws ChannelKeyIsSetException {

        if (this.key != null) {
            throw new ChannelKeyIsSetException();
        }

        this.key = key;
    }


    public synchronized String getKey() {
        return this.key;
    }


    public synchronized void clearKey() {
        this.key = null;
    }


    public synchronized void setUserLimit(int userLimit)
        throws IllegalArgumentException {

        if (userLimit < 1) {
            throw new IllegalArgumentException("User limit must be greater than 1");
        }

        this.userLimit = userLimit;
    }

    public synchronized int getUserLimit() {
        return this.userLimit;
    }
}

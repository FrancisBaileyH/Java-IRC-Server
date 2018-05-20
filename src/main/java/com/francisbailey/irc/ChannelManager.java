package com.francisbailey.irc;

import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fbailey on 01/12/16.
 */
public class ChannelManager {

    private HashMap<String, Channel> channels;
    private final static String[] channelPrefixes = {"&" , "!", "+", "#"};

    /**
     * Loop through the configuration and generate channels.
     * @param channels
     */
    public ChannelManager(List<HierarchicalConfiguration> channels) {

        this.channels = new HashMap<>();

        for (HierarchicalConfiguration channel: channels) {
            String chanName = channel.getString("name");
            String topic = channel.getString("topic");
            this.addChannel(chanName, topic);
        }
    }


    /**
     * Add a new channel to the channel manager.
     * @param name
     * @param topic
     */
    public void addChannel(String name, String topic) {

        this.channels.put(name, new Channel(name, topic));
    }


    public void addChannel(Channel channel) {
        this.channels.put(channel.getName(), channel);
    }


    public void removeChannel(String name) {
        this.channels.remove(name);
    }


    /**
     * Channels names are strings (beginning with a '&', '#', '+' or '!'
     * character) of length up to fifty (50) characters.  Channel names are
     * case insensitive.
     * Check if target is an existing channel
     * @param target
     * @return
     */
    public boolean isChannel(String target) {

        return isChannelType(target) && channels.containsKey(target);
    }


    public boolean isValidChannelName(String name) {

        if (!isChannelType(name)) {
            return false;
        }

        if (name.length() > 50) {
            return false;
        }

        return true;
    }


    /**
     *
     * @param target
     * @return
     */
    public static boolean isChannelType(String target) {

        for (int i = 0; i < channelPrefixes.length; i++) {
            if (target.startsWith(channelPrefixes[i])) {
                return true;
            }
        }

        return false;
    }


    /**
     * Retrieve channel object by name.
     * @param channel
     * @return
     */
    public Channel getChannel(String channel) {

        return channels.get(channel);
    }


    /**
     * Fetch all channels that a user is currently participating in
     * @param c
     * @return
     */
    public ArrayList<Channel> getChannelsByUser(Connection c) {

        ArrayList<Channel> userIn = new ArrayList<>();

        for (Channel chan: this.channels.values()) {
            if (chan.hasUser(c)) {
                userIn.add(chan);
            }
        }

        return userIn;
    }


    /**
     * Retrieve all channel objects.
     * @return
     */
    public Collection<Channel> getChannels() {

        return this.channels.values();
    }


    /**
     * Check if a channel exists.
     * @param chanName
     * @return
     */
    public Boolean hasChannel(String chanName) {

        return this.channels.get(chanName) != null;
    }

}

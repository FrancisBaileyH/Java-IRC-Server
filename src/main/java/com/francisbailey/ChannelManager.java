package com.francisbailey;

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

    /**
     * Loop through the configuration and generate channels.
     * @param channels
     */
    public ChannelManager(List<HierarchicalConfiguration> channels) {

        this.channels = new HashMap<>();

        for (HierarchicalConfiguration channel: channels) {

            String chanName = channel.getString("name");
            String topic = channel.getString("topic");
            Channel c = new Channel(chanName, topic);
            this.channels.put(chanName, c);
        }
    }


    /**
     * Check if target is an existing channel
     * @param target
     * @return
     */
    public Boolean isChannel(String target) {

        return target.startsWith("#") && channels.containsKey(target);
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

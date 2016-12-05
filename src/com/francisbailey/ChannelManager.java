package com.francisbailey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by fbailey on 01/12/16.
 */
public class ChannelManager {

    private HashMap<String, Channel> channels;

    /**
     * @TODO - dynamic channel topics
     * @param channelNames
     */
    public ChannelManager(String[] channelNames) {

        this.channels = new HashMap<>();

        for (String chanName: channelNames) {

            Channel c = new Channel(chanName, "Test topic");
            this.channels.put(chanName, c);
        }
    }


    public Boolean isChannel(String target) {

        return target.startsWith("#") && channels.containsKey(target);
    }


    /**
     *
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
     *
     * @return
     */
    public Collection<Channel> getChannels() {

        return this.channels.values();
    }


    /**
     *
     * @param chanName
     * @return
     */
    public Boolean hasChannel(String chanName) {

        return this.channels.get(chanName) != null;
    }

}

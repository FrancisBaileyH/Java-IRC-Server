package com.francisbailey.irc;

import java.util.ArrayList;

/**
 * Created by fbailey on 10/05/17.
 */
public class ChannelModes {

    /**
     * @TODO - Last Working On this
     * - Need to check against user modes
     * - Need to check against channel modes
     * - RequiresContext?
     * Need to consider that there's modes directly applied to a user
     * and modes directly applied to the channel
     *
     * Channel User Modes
     * O, o, v
     *
     * Channel Modes
     * a, i, m, n, q, p, s, r ,t ,k, l, b, e
     */
    private ArrayList<String> userChanModes;
    private ArrayList<String> chanModes;


    public ChannelModes() {

        this.userChanModes = new ArrayList<>();
        this.userChanModes.add("O");
        this.userChanModes.add("o");
        this.userChanModes.add("v");

        this.chanModes = new ArrayList<>();
        this.chanModes.add("a");
        this.chanModes.add("i");
        this.chanModes.add("m");
        this.chanModes.add("n");
        this.chanModes.add("q");
        this.chanModes.add("p");
        this.chanModes.add("s");
        this.chanModes.add("r");
        this.chanModes.add("t");
        this.chanModes.add("k");
        this.chanModes.add("l");
        this.chanModes.add("b");
        this.chanModes.add("e");
    }


    public Boolean isChanMode(String mode) {
        return this.chanModes.contains(mode);
    }


    public Boolean isChanUserMode(String mode) {
        return this.userChanModes.contains(mode);
    }


}

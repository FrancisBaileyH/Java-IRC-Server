package com.francisbailey.irc.modes;


import java.util.ArrayList;
import java.util.HashMap;


public class ModeSet {


    public final static HashMap<String, ChannelMode> chanModes = new HashMap<String, ChannelMode>() {{
        put("v", new ChannelMode("v", "VOICE", true, ChannelUserModeStrategy.class));
        put("O", new ChannelMode("O", "OWNER", true, ChannelUserModeStrategy.class));
        put("o", new ChannelMode("o", "OPERATOR", true, ChannelUserModeStrategy.class));
        put("a", new ChannelMode("a", "ANONYMOUS", false, StandardChannelModeStrategy.class));
    }};
//    ANONYMOUS("a"),
//    INVITE_ONLY("i"),
//    MODERATED("m"),
//    CHANNEL_SOURCE_ONLY("n"),
//    QUIET("q"),
//    PRIVATE("p"),
//    SECRET("s"),
//    REOP("r"),
//    OP_TOPIC_ONLY("t");

    /**
     * There is still:
     * k - channel key
     * l - user limit
     * b - ban mask
     * e - ban mask exception (override)
     * I - invitation mask
    */

//    public final static HashMap<String, Mode> chanModes;
//    static {
//        HashMap<String, Mode> modeMap = new HashMap<>();
//        modeMap.put()
//
//
//    }

//    public final static HashMap<String, Mode> chanModes = new HashMap<String, Mode>() {{
//        put("a", new Mode("a", "ANONYMOUS"));
//        put("i", new Mode("i", "INVITE_ONLY"));
//        add(new Mode("m", "MODERATED"));
//        add(new Mode("n", "CHANNEL_SRC_ONLY"));
//    }};

//    INVISIBLE("i"),
//    AWAY("a"),
//    WALLOPS("w"),
//    RESTRICTED("r"),
//    OPERATOR("o"),
//    LOCAL_OPERATOR("O"),
//    SNOTICE("s"),
//    VOICE("v");
    public final static ArrayList<Mode> userModes = new ArrayList<>();






    private ArrayList<Mode> modes;

    public ModeSet() {
        this.modes = new ArrayList<>();
    }

    /**
     * Add a mode to a mode set if does not exist already
     *
     * @param mode
     * @return true if the mode exists and can be added
     */
    public synchronized boolean addMode(Mode mode) {

        if (!this.modes.contains(mode)) {
            this.modes.add(mode);

            return true;
        }

        return false;
    }

    /**
     * Check if a mode exists
     *
     * @param mode
     * @return true if the mode exists
     */
    public synchronized boolean hasMode(Mode mode) {
        return this.modes.contains(mode);
    }

    /**
     * Remove a mode
     *
     * @param mode
     * @return true if the mode exists and can be removed
     */
    public synchronized boolean removeMode(Mode mode) {

        if (this.modes.contains(mode)) {
            this.modes.remove(mode);

            return true;
        }

        return false;
    }

    /**
     * Clear all modes
     */
    public synchronized void clearModes() {
        this.modes.clear();
    }


    /**
     * Get concatenated string of all modes in the set
     *
     * @return String with all flags
     */
    public synchronized String getModes() {
        String output = "";

        for (Mode mode: this.modes) {
            output += mode.getFlag();
        }

        return output;
    }

    /**
     *
     * @return String with all flags
     */
    public String toString() {
        return this.getModes();
    }

}
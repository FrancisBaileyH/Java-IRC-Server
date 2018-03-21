package com.francisbailey.irc.mode;


import com.francisbailey.irc.mode.strategy.ChannelUserModeStrategy;
import com.francisbailey.irc.mode.strategy.StandardChannelModeStrategy;

import java.util.ArrayList;
import java.util.HashMap;


public class ModeSet {

    /**
     * There is still:
     * k - channel key
     * l - user limit
     * b - ban mask
     * e - ban mask exception (override)
     * I - invitation mask
     */

    public static final ChannelMode CHAN_OPERATOR = new ChannelMode("O", "CHAN_OPERATOR", true, ChannelUserModeStrategy.class);
    public static final ChannelMode CHAN_VOICE    = new ChannelMode("v", "CHAN_VOICE", true, ChannelUserModeStrategy.class);
    public static final ChannelMode OWNER         = new ChannelMode("O", "OWNER", true, ChannelUserModeStrategy.class);
    public static final ChannelMode ANONYMOUS     = new ChannelMode("a", "ANONYMOUS", false, StandardChannelModeStrategy.class);
    public static final ChannelMode INVITE        = new ChannelMode("i", "INVITE", false, StandardChannelModeStrategy.class);
    public static final ChannelMode MODERATED     = new ChannelMode("m", "MODERATED", false, StandardChannelModeStrategy.class);
    public static final ChannelMode CHAN_SRC_ONLY = new ChannelMode("n", "CHAN_SRC_ONLY", false, StandardChannelModeStrategy.class);
    public static final ChannelMode QUIET         = new ChannelMode("q", "QUIET", false, StandardChannelModeStrategy.class);
    public static final ChannelMode PRIVATE       = new ChannelMode("p", "PRIVATE", false, StandardChannelModeStrategy.class);
    public static final ChannelMode SECRET        = new ChannelMode("s", "SECRET", false, StandardChannelModeStrategy.class);
    public static final ChannelMode REOP          = new ChannelMode("r", "REOP", false, StandardChannelModeStrategy.class);
    public static final ChannelMode OP_TOPIC_ONLY = new ChannelMode("t", "OP_TOPIC_ONLY", false, StandardChannelModeStrategy.class);

    public final static HashMap<String, ChannelMode> chanModes = new HashMap<String, ChannelMode>();
    static {
        chanModes.put(CHAN_VOICE.getFlag(),    CHAN_VOICE);
        chanModes.put(OWNER.getFlag(),         OWNER);
        chanModes.put(CHAN_OPERATOR.getFlag(), CHAN_OPERATOR);
        chanModes.put(INVITE.getFlag(),        INVITE);
        chanModes.put(MODERATED.getFlag(),     MODERATED);
        chanModes.put(CHAN_SRC_ONLY.getFlag(), CHAN_SRC_ONLY);
        chanModes.put(QUIET.getFlag(),         QUIET);
        chanModes.put(PRIVATE.getFlag(),       PRIVATE);
        chanModes.put(SECRET.getFlag(),        SECRET);
        chanModes.put(REOP.getFlag(),          REOP);
        chanModes.put(OP_TOPIC_ONLY.getFlag(), OP_TOPIC_ONLY);
    };



    public static final Mode OPERATOR       = new Mode("O", "OPERATOR");
    public static final Mode LOCAL_OPERATOR = new Mode("o", "LOCAL_OPERATOR");
    public static final Mode AWAY           = new Mode("a", "AWAY");
    public static final Mode WALLOPS        = new Mode("w", "WALLOPS");
    public static final Mode RESTRICTED     = new Mode("r", "RESTRICTED");
    public static final Mode SNOTICE        = new Mode("s", "SNOTICE");
    public static final Mode VOICE          = new Mode("o", "LOCAL_OPERATOR");
    public static final Mode INVISIBLE      = new Mode("i", "INVISIBLE");

    public final static HashMap<String, Mode> userModes = new HashMap<String, Mode>();
    static {
        userModes.put(OPERATOR.getFlag(),       OPERATOR);
        userModes.put(LOCAL_OPERATOR.getFlag(), LOCAL_OPERATOR);
        userModes.put(AWAY.getFlag(),           AWAY);
        userModes.put(WALLOPS.getFlag(),        WALLOPS);
        userModes.put(RESTRICTED.getFlag(),     RESTRICTED);
        userModes.put(SNOTICE.getFlag(),        SNOTICE);
        userModes.put(VOICE.getFlag(),          VOICE);
        userModes.put(INVISIBLE.getFlag(),      INVISIBLE);
    };



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
     * Clear all mode
     */
    public synchronized void clearModes() {
        this.modes.clear();
    }


    /**
     * Get concatenated string of all mode in the set
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
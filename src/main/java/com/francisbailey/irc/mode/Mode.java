package com.francisbailey.irc.mode;

import java.util.HashMap;

/**
 * Created by fbailey on 19/03/18.
 */
public class Mode {

    /**
     * How do we want to do strategies?
     * Really this will be a hardcoded value
     * The thing is we need access to the instance don't we?
     * Or do we? Perhaps we need to change the way messages are sent?
     */

    private final String flag;
    private final String name;
    private final boolean requiresArg;

    public static final Mode CHAN_OPERATOR      = new Mode("O", "CHAN_OPERATOR", true);
    public static final Mode CHAN_VOICE         = new Mode("v", "CHAN_VOICE", true);
    public static final Mode OWNER              = new Mode("O", "OWNER", true);
    public static final Mode ANONYMOUS          = new Mode("a", "ANONYMOUS");
    public static final Mode INVITE             = new Mode("i", "INVITE");
    public static final Mode MODERATED          = new Mode("m", "MODERATED");
    public static final Mode CHAN_SRC_ONLY      = new Mode("n", "CHAN_SRC_ONLY");
    public static final Mode QUIET              = new Mode("q", "QUIET");
    public static final Mode PRIVATE            = new Mode("p", "PRIVATE");
    public static final Mode SECRET             = new Mode("s", "SECRET");
    public static final Mode REOP               = new Mode("r", "REOP");
    public static final Mode OP_TOPIC_ONLY      = new Mode("t", "OP_TOPIC_ONLY");
    public static final Mode CHAN_KEY           = new Mode("k", "CHAN_KEY", true);
    public static final Mode USER_LIMIT         = new Mode("l", "USER_LIMIT");
    public static final Mode BAN_MASK           = new Mode("b", "BAN_MASK");
    public static final Mode BAN_MASK_EXCEPTION = new Mode("e", "BAN_MASK_EXCEPTION");
    public static final Mode INVITATION_MASK    = new Mode("I", "INVITATION_MASK", true);
    public static final Mode OPERATOR           = new Mode("O", "OPERATOR");
    public static final Mode LOCAL_OPERATOR     = new Mode("o", "LOCAL_OPERATOR");
    public static final Mode AWAY               = new Mode("a", "AWAY");
    public static final Mode WALLOPS            = new Mode("w", "WALLOPS");
    public static final Mode RESTRICTED         = new Mode("r", "RESTRICTED");
    public static final Mode SNOTICE            = new Mode("s", "SNOTICE");
    public static final Mode VOICE              = new Mode("o", "LOCAL_OPERATOR");
    public static final Mode INVISIBLE          = new Mode("i", "INVISIBLE");

    public final static HashMap<String, Mode> channelModes = new HashMap<>();
    static {
        channelModes.put(CHAN_VOICE.getFlag(),         CHAN_VOICE);
        channelModes.put(OWNER.getFlag(),              OWNER);
        channelModes.put(CHAN_OPERATOR.getFlag(),      CHAN_OPERATOR);
        channelModes.put(INVITE.getFlag(),             INVITE);
        channelModes.put(MODERATED.getFlag(),          MODERATED);
        channelModes.put(CHAN_SRC_ONLY.getFlag(),      CHAN_SRC_ONLY);
        channelModes.put(QUIET.getFlag(),              QUIET);
        channelModes.put(PRIVATE.getFlag(),            PRIVATE);
        channelModes.put(SECRET.getFlag(),             SECRET);
        channelModes.put(REOP.getFlag(),               REOP);
        channelModes.put(OP_TOPIC_ONLY.getFlag(),      OP_TOPIC_ONLY);
        channelModes.put(CHAN_KEY.getFlag(),           CHAN_KEY);
        channelModes.put(USER_LIMIT.getFlag(),         USER_LIMIT);
        channelModes.put(BAN_MASK.getFlag(),           BAN_MASK);
        channelModes.put(BAN_MASK_EXCEPTION.getFlag(), BAN_MASK_EXCEPTION);
        channelModes.put(INVITATION_MASK.getFlag(),    INVITATION_MASK);
    }

    public final static HashMap<String, Mode> userModes = new HashMap<>();
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

    public Mode(final String flag, final String name, final boolean requiresArg) {
        this.flag = flag;
        this.name = name;
        this.requiresArg = requiresArg;
    }


    public Mode(final String flag, final String name) {
        this(flag, name, false);
    }


    public final String getFlag() {
        return this.flag;
    }


    public final String getName() {
        return this.name;
    }


    public boolean requiresArg() {
        return this.requiresArg;
    }


    public boolean equals(Mode m) {
        return m.getFlag().equals(this.flag);
    }


    public boolean equals(String m) {
        return m.equals(this.flag);
    }
}


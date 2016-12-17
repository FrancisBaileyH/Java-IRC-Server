package com.francisbailey;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by fbailey on 13/12/16.
 */
public class UserMode {

    private EnumSet<ModeType> mode = EnumSet.noneOf(ModeType.class);
    public static Map<String, ModeType> modeStringMap = createImmutableMap();

    /**
     * Modes:
     *
     * a - away
     * i - invisible
     * w - wallops
     * r - restricted user conn
     * o - operator
     * O - local operator
     * s - receives server notices
     */
    public enum ModeType {
        AWAY("a"),
        INVISIBLE("i"),
        WALLOPS("w"),
        RESTRICTED("r"),
        OPERATOR("o"),
        LOCAL_OPERATOR("O"),
        SERVER_NOTICES("s");

        private final String text;

        ModeType(final String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }


    /**
     * Append a mode to our mode set
     * @param mode
     */
    public void addMode(ModeType mode) {

        if (!this.mode.contains(mode)) {
            this.mode.add(mode);
        }
    }


    /**
     * Remove a mode from our mode set
     * @param mode
     */
    public void unsetMode(ModeType mode) {

        if (this.mode.contains(mode)) {
            this.mode.remove(mode);
        }
    }


    /**
     * Output the mode set as a string with no spaces
     * containing the string representation of each mode flag
     * @return
     */
    public String toString() {

        String modes = "";

        for (ModeType m: this.mode) {

            modes += m.toString();
        }

        return modes;
    }


    /**
     * Create an immutable map, to map the corresponding string values
     * to our internally used Mode Flags
     * @return
     */
    private static Map<String, ModeType> createImmutableMap() {

        Map<String, ModeType> result = new HashMap<>();
        result.put("a", ModeType.AWAY);
        result.put("i", ModeType.INVISIBLE);
        result.put("w", ModeType.WALLOPS);
        result.put("r", ModeType.RESTRICTED);
        result.put("o", ModeType.OPERATOR);
        result.put("O", ModeType.LOCAL_OPERATOR);
        result.put("s", ModeType.SERVER_NOTICES);

        return Collections.unmodifiableMap(result);
    }


 }

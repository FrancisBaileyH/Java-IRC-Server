package com.francisbailey.irc.modes;


public enum UserModes {
    INVISIBLE("i"),
    AWAY("a"),
    WALLOPS("w"),
    RESTRICTED("r"),
    OPERATOR("o"),
    LOCAL_OPERATOR("O"),
    SNOTICE("s"),
    VOICE("v");

    private final String mode;


    UserModes(final String mode) {
        this.mode = mode;
    }


    public String toString() {
        return this.mode;
    }

    public static boolean contains(String mode) {
         for (UserModes umode: UserModes.values()) {
             if (umode.name().toString().equals(mode)) {
                 return true;
             }
         }

         return false;
    }
}
package com.francisbailey.irc.mode;

/**
 * Created by fbailey on 19/03/18.
 */
public class Mode {

    private final String flag;
    private final String name;
    private final boolean requiresArg;


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


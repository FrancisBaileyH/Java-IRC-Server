package com.francisbailey.irc;

import java.util.ArrayList;

/**
 * Created by fbailey on 10/05/17.
 */
public class UserModes {

    private ArrayList<String> modes;

    public UserModes() {

        this.modes = new ArrayList<>();
        this.modes.add("i");
        this.modes.add("a");
        this.modes.add("w");
        this.modes.add("r");
        this.modes.add("o");
        this.modes.add("O");
        this.modes.add("s");
    }


    public Boolean isMode(String mode) {
        return this.modes.contains(mode);
    }

}

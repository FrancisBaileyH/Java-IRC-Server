package com.francisbailey.irc;

import java.util.ArrayList;

/**
 * Created by fbailey on 10/05/17.
 */
public class Modes {

    private ArrayList<String> modes;


    public Modes() {
        this.modes = new ArrayList<>();
    }


    public void addMode(String mode) {
        if (!modes.contains(mode)) {
            modes.add(mode);
        }
    }


    public Boolean hasMode(String mode) {
        return modes.contains(mode);
    }


    public void unsetMode(String mode) {
        if (modes.contains(mode)) {
            modes.remove(mode);
        }
    }


    public String getModeFlags() {

        String output = "";

        for (String mode: modes) {
            output += mode;
        }

        return output;
    }

}

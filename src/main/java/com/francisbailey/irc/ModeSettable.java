package com.francisbailey.irc;

import java.util.ArrayList;

/**
 * Created by fbailey on 10/05/17.
 */
public interface ModeSettable {

    ArrayList<String> modes = new ArrayList<>();


    default void addMode(String mode) {
        if (!modes.contains(mode)) {
            modes.add(mode);
        }
    }


    default Boolean hasMode(String mode) {
        return modes.contains(mode);
    }


    default void unsetMode(String mode) {
        if (modes.contains(mode)) {
            modes.remove(mode);
        }
    }


    default String getModes() {

        String output = "";

        for (String mode: modes) {
            output += mode;
        }

        return output;
    }

}

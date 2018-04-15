package com.francisbailey.irc.mode;


import java.util.ArrayList;


public class ModeSet {


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
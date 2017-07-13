package com.francisbailey.irc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fbailey on 12/07/17.
 */
public class ModeControl {


    private HashMap<ModeContext, ArrayList<String>> resources;
    private HashMap<ModeTarget, Modes> targetModes;


    public ModeControl() {

        this.resources = new HashMap<>();
        this.targetModes = new HashMap<>();
    }


    /**
     * Add a mode to a target for a given context. If a mode
     * is being set for the first time, initialize a new Modes
     * object.
     * @param mode
     * @param target
     * @param context
     */
    public void addTargetMode(String mode, ModeTarget target, ModeContext context) {

        Modes modes = this.targetModes.get(target);

        if (modes == null) {
            modes = new Modes();
            this.targetModes.put(target, modes);
        }

        modes.addMode(context, mode);
    }


    /**
     * Remove a target's modes in every context
     * @param target
     */
    public void removeTarget(ModeTarget target) {

        this.targetModes.remove(target);
    }


    /**
     * Remove a mode from a given target and context
     * @param mode
     * @param target
     * @param context
     */
    public void removeTargetMode(String mode, ModeTarget target, ModeContext context) {

        if (this.targetModes.containsKey(target)) {
            this.targetModes.get(target).unsetMode(context, mode);
        }
    }


    /**
     * Verify that a target has a mode for a particular context
     * @param mode
     * @param target
     * @param context
     * @return
     */
    public Boolean targetHasMode(String mode, ModeTarget target, ModeContext context) {

        if (!this.targetModes.containsKey(target)) {
            return false;
        }
        else {
            return this.targetModes.get(target).hasMode(context, mode);
        }
    }


    /**
     * Get all target modes as a single string for a given context
     * @param target
     * @param context
     * @return
     */
    public String getTargetModes(ModeTarget target, ModeContext context) {

        String output = "";
        Modes modes = this.targetModes.get(target);

        if (modes != null) {
            output = modes.getModeFlags(context);
        }

        return output;
    }


    /**
     * Check that a mode exists for a give resource, by
     * seeing if the resource exists and if there's a mode
     * for that resource.
     * @param mode
     * @param context
     * @return
     */
    public Boolean isValidMode(String mode, ModeContext context) {

        ArrayList<String> modes = this.resources.get(context);

        if (modes == null || !modes.contains(mode)) {
            return false;
        }

        return true;
    }


    /**
     * Add a new mode type to a context. If the context doesn't
     * exist, create an entry for it and add the new mode.
     * @param mode
     * @param context
     */
    public void addModeTypeForContext(String mode, ModeContext context) {

        ArrayList<String> modes = this.resources.get(context);

        if (modes == null) {
            modes = new ArrayList<>();
            this.resources.put(context, modes);
        }

        modes.add(mode);
    }


    /**
     * Add a new context if it hasn't been added yet.
     * @param context
     */
    public void addModeContext(ModeContext context) {

        if (!this.resources.containsKey(context)) {
            this.resources.put(context, new ArrayList<>());
        }
    }


    /**
     * Remove a context from our control and delete all associated
     * modes in the process.
     * @param context
     */
    public void removeModeContext(ModeContext context) {

        if (this.resources.containsKey(context)) {
            this.resources.remove(context);
        }
    }


    /**
     * Remove a mode type for a given context if it exists.
     * @param mode
     * @param context
     */
    public void removeModeTypeForContext(String mode, ModeContext context) {

        ArrayList<String> modes = this.resources.get(context);

        if (modes != null && modes.contains(mode)) {
            modes.remove(mode);
        }
    }

}

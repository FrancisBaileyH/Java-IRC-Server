package com.francisbailey.irc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fbailey on 12/07/17.
 */
public class ModeControl {


    private HashMap<String, ArrayList<String>> resources;
    private HashMap<ModeTarget, Modes> targetModes;
    private HashMap<String, ModeContext> contextCache;


    public ModeControl() {
        this(new HashMap<>());
    }


    public ModeControl(HashMap<String, String[]> defaultResourceModes) {

        this.resources = new HashMap<>();
        this.targetModes = new HashMap<>();
        this.contextCache = new HashMap<>();

        for (String resourceType: defaultResourceModes.keySet()) {
            for (String mode: defaultResourceModes.get(resourceType)) {
                this.addModeTypeForContext(mode, resourceType);
            }
        }
    }


    /**
     * Add a mode to a target for a given context. If a mode
     * is being set for the first time, initialize a new Modes
     * object.
     * @param mode
     * @param target
     * @param resource
     */
    public void addTargetMode(String mode, ModeTarget target, ModeResource resource) {

        Modes modes = this.targetModes.get(target);

        if (modes == null) {
            modes = new Modes();
            this.targetModes.put(target, modes);
        }

        modes.addMode(this.buildModeContext(target, resource), mode);
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
     * @param resource
     */
    public void removeTargetMode(String mode, ModeTarget target, ModeResource resource) {

        if (this.targetModes.containsKey(target)) {
            this.targetModes.get(target).unsetMode(this.buildModeContext(target, resource), mode);
        }
    }


    /**
     * Verify that a target has a mode for a particular context
     * @param mode
     * @param target
     * @param resource
     * @return
     */
    public Boolean targetHasMode(String mode, ModeTarget target, ModeResource resource) {

        if (!this.targetModes.containsKey(target)) {
            return false;
        }
        else {
            return this.targetModes.get(target).hasMode(this.buildModeContext(target, resource), mode);
        }
    }


    /**
     * Get all target modes as a single string for a given context
     * @param target
     * @param resource
     * @return
     */
    public String getTargetModes(ModeTarget target, ModeResource resource) {

        String output = "";
        Modes modes = this.targetModes.get(target);

        if (modes != null) {
            output = modes.getModeFlags(this.buildModeContext(target, resource));
        }

        return output;
    }


    /**
     * Check that a mode exists for a given resource, by
     * seeing if the resource exists and if there's a mode
     * for that resource.
     * @param mode
     * @param context
     * @return
     */
    public Boolean isValidMode(String mode, ModeContext context) {

        ArrayList<String> modes = this.resources.get(context.getContextName());

        if (modes == null || !modes.contains(mode)) {
            return false;
        }

        return true;
    }


    public Boolean isValidMode(String mode, ModeTarget target, ModeResource resource) {

        return this.isValidMode(mode, new ModeContext(target, resource));
    }


    /**
     * Add a new mode type to a context. If the context doesn't
     * exist, create an entry for it and add the new mode.
     * @param mode
     * @param context
     */
    public void addModeTypeForContext(String mode, ModeContext context) {

        this.addModeTypeForContext(mode, context.getContextName());
    }


    private void addModeTypeForContext(String mode, String contextName) {

        ArrayList<String> modes = this.resources.get(contextName);

        if (modes == null) {
            modes = new ArrayList<>();
            this.resources.put(contextName, modes);
        }

        modes.add(mode);
    }


    /**
     * Add a new context if it hasn't been added yet.
     * @param context
     */
    public void addModeContext(ModeContext context) {

        if (!this.resources.containsKey(context.getContextName())) {
            this.resources.put(context.getContextName(), new ArrayList<>());
        }
    }


    /**
     * Remove a context from our control and delete all associated
     * modes in the process.
     * @param context
     */
    public void removeModeContext(ModeContext context) {

        if (this.resources.containsKey(context.getContextName())) {
            this.resources.remove(context.getContextName());
        }
    }


    /**
     * Remove a mode type for a given context if it exists.
     * @param mode
     * @param context
     */
    public void removeModeTypeForContext(String mode, ModeContext context) {

        ArrayList<String> modes = this.resources.get(context.getContextName());

        if (modes != null && modes.contains(mode)) {
            modes.remove(mode);
        }
    }


    public ModeContext buildModeContext(ModeTarget target, ModeResource resource) {

        String contextName = ModeContext.getContextName(target, resource);

        if (this.contextCache.containsKey(contextName)) {
            return this.contextCache.get(contextName);
        }
        else {
            ModeContext context = new ModeContext(target, resource);
            this.contextCache.put(context.getContextName(), context);
            return context;
        }
    }

}

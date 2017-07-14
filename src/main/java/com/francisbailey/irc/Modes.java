package com.francisbailey.irc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fbailey on 10/05/17.
 */
public class Modes {

    private HashMap<String, ArrayList<String>> modes;


    public Modes() {
        this.modes = new HashMap<>();
    }


    /**
     * Add user modes to a given context. Context in this case
     * can be a channel name, a server name, etc.
     *
     * @param context
     * @param mode
     */
    public synchronized void addMode(ModeContext context, String mode) {

        String contextName = context.getContextName();

        if (!modes.containsKey(contextName)) {
            modes.put(contextName, new ArrayList<>());
        }

        ArrayList<String> contextualModes = modes.get(contextName);

        if (!contextualModes.contains(mode)) {
            contextualModes.add(mode);
        }
    }


    /**
     * Check if a mode is set for a given context
     *
     * @param context
     * @param mode
     * @return
     */
    public synchronized Boolean hasMode(ModeContext context, String mode) {

        String contextName = context.getContextName();
        return modes.containsKey(contextName) && modes.get(contextName).contains(mode);
    }


    /**
     * Unset a mode for a given context
     *
     * @param context
     * @param mode
     */
    public synchronized void unsetMode(ModeContext context, String mode) {

        String contextName = context.getContextName();

        if (modes.containsKey(contextName)) {
            modes.get(contextName).remove(mode);
        }
    }


    /**
     * Get a string representation of all set flags
     * for a given context. Useful for displaying
     * modes to users that query the server.
     *
     * @param context
     * @return
     */
    public synchronized String getModeFlags(ModeContext context) {

        String output = "";

        if (modes.containsKey(context.getContextName())) {
            for (String mode : modes.get(context.getContextName())) {
                output += mode;
            }
        }

        return output;
    }

}



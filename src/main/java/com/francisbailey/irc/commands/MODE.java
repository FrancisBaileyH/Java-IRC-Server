package com.francisbailey.irc.commands;

import com.francisbailey.irc.*;
import com.francisbailey.irc.commands.internal.CHANMODE;
import com.francisbailey.irc.commands.internal.USERMODE;

/**
 * Created by fbailey on 13/12/16.
 */
public class MODE implements Executable {


    /**
     * The mode command target can be a channel or a user,
     * so we'll decide what the target is and defer the
     * execution to our internal mode commands: CHANMODE or USERMODE
     * @param c
     * @param cm
     * @param instance
     */
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String target = cm.getParameter(0);
        ChannelManager channelManager = instance.getChannelManager();

        if (channelManager.isChannelType(target)) {
            Executable exe = new CHANMODE();
            exe.execute(c, cm, instance);
        }
        else {
            Executable exe = new USERMODE();
            exe.execute(c, cm, instance);
        }
    }


    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }
}

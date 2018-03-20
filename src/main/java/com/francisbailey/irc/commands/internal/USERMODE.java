package com.francisbailey.irc.commands.internal;


import com.francisbailey.irc.*;
import com.francisbailey.irc.modes.ModeSet;
import com.francisbailey.irc.modes.UserModes;

/**
 * Created by fbailey on 07/05/17.
 */
public class USERMODE implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String targetNick = cm.getParameter(0);
        String nick = c.getClientInfo().getNick();

        // Check if nick matches user
        if (!targetNick.equals(nick) && c.getModes().hasMode(UserModes.OPERATOR.toString())) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_USERSDONTMATCH, ": Can't change mode for other users"));
        }
        else if (cm.getParameterCount() < 2) {
            this.sendUsermode(c, instance);
        }
        else {

            String modeAction = cm.getParameter(1);

            if (modeAction.length() != 2 || !UserModes.contains(modeAction.substring(1))
            || (!modeAction.startsWith("+") && !modeAction.startsWith("-"))) {

                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, ": Unknown umode flag"));
            }
            else {
                String action = modeAction.substring(0, 1);
                String mode = modeAction.substring(1);

                if (action.equals("-")) {
                    this.handleRemoveMode(c, instance, mode);
                }
                else if (action.equals("+")) {
                    this.handleAddMode(c, instance, mode);
                }

                this.sendUsermode(c, instance);
            }
        }
    }


    /**
     * Notify the user of their current user mode
     * @param c
     */
    public void sendUsermode(Connection c, ServerManager instance) {

        String nick = c.getClientInfo().getNick();
        String modes = c.getModes().toString();
        c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_UMODEIS, nick + " :+" + modes));
    }


    /**
     * IRC protocol states that modes o, O, a should not be set
     * via MODE command
     */
    private void handleAddMode(Connection c, ServerManager instance, String mode) {

        if (!mode.equals("o") && !mode.equals("O") && !mode.equals("a")) {
            ModeSet ms = c.getModes();
            ms.addMode(mode);
            c.setModes(ms);
        }
    }


    /**
     * IRC protocol states that users should not be able to de-restrict
     * themselves
     * @param c
     * @param mode
     */
    private void handleRemoveMode(Connection c, ServerManager instance, String mode) {

        if (!mode.equals("r")) {
            ModeSet ms = c.getModes();
            ms.removeMode(mode);
            c.setModes(ms);
        }
    }


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public Boolean canExecuteUnregistered() {
        return false;
    }
}

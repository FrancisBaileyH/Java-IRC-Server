package com.francisbailey.irc.commands.internal;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 07/05/17.
 */
public class USERMODE implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        // Check if nick matches user
        if (!c.getClientInfo().getNick().equals(cm.getParameter(0))) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_USERSDONTMATCH, ": Can't change mode for other users"));
        }
        else if (cm.getParameterCount() < 2) {
            this.sendUsermode(c, instance);
        }
        else {

            String modeAction = cm.getParameter(1);
            ModeControl mc = instance.getModeControl();

            if (modeAction.length() != 2 || !mc.isValidMode(modeAction.substring(1), (ModeTarget)c, instance)
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
        String modeis = instance.getModeControl().getTargetModes((ModeTarget)c, instance);
        c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_UMODEIS, nick + " :+" + modeis));
    }


    /**
     * IRC protocol states that modes o, O, a should not be set
     * via MODE command
     */
    private void handleAddMode(Connection c, ServerManager instance, String mode) {

        if (!mode.equals("o") && !mode.equals("O") && !mode.equals("a")) {
            instance.getModeControl().addTargetMode(mode, (ModeTarget)c, instance);
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
            instance.getModeControl().removeTargetMode(mode, (ModeTarget)c, instance);
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

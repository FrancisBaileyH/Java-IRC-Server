package com.francisbailey.irc.command.internal;


import com.francisbailey.irc.*;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

/**
 * Created by fbailey on 07/05/17.
 */
public class USERMODE implements Executable {

    @Override
    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        String targetNick = cm.getParameter(0);
        String nick = c.getClientInfo().getNick();

        Connection target = instance.findConnectionByNick(targetNick);

        // Check if nick matches user
        if (!targetNick.equals(nick) && !c.getModes().hasMode(ModeSet.OPERATOR)) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_USERSDONTMATCH, nick + " :Can't change mode for other users"));
        }
        else if (target == null) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_NOSUCHNICK, nick + " :No such user"));
        }
        else if (cm.getParameterCount() < 2) {
            this.sendUsermode(c, target, instance);
        }
        else {
            String modeAction = cm.getParameter(1);

            if (modeAction.length() != 2 || !ModeSet.userModes.containsKey(modeAction.substring(1))
            || (!modeAction.startsWith("+") && !modeAction.startsWith("-"))) {

                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, nick + " :Unknown umode flag"));
            } else {
                String operation = modeAction.substring(0, 1);
                String flag = modeAction.substring(1);

                Mode mode = ModeSet.userModes.get(flag);

                if (operation.equals("-")) {
                    this.handleRemoveMode(target, instance, mode);
                }
                else if (operation.equals("+")) {
                    this.handleAddMode(target, instance, mode);
                }

                this.sendUsermode(c, target, instance);
            }
        }
    }


    /**
     * Notify the user of their current user mode
     * @param c
     */
    public void sendUsermode(Connection c, Connection target, ServerManager instance) {

        String nick = target.getClientInfo().getNick();
        String modes = target.getModes().toString();
        c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_UMODEIS, nick + " :+" + modes));
    }


    /**
     * IRC protocol states that mode o, O, a should not be set
     * via MODE command
     */
    private void handleAddMode(Connection c, ServerManager instance, Mode mode) {

        if (!mode.equals(ModeSet.OPERATOR) && !mode.equals(ModeSet.LOCAL_OPERATOR) && !mode.equals(ModeSet.AWAY)) {
            c.getModes().addMode(mode);
        }
    }


    /**
     * IRC protocol states that users should not be able to de-restrict
     * themselves
     * @param c
     * @param mode
     */
    private void handleRemoveMode(Connection c, ServerManager instance, Mode mode) {

        if (!mode.equals(ModeSet.RESTRICTED)) {
            c.getModes().removeMode(mode);
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

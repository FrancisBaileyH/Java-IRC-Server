package com.francisbailey.irc.command.internal;


import com.francisbailey.irc.*;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.strategy.UserModeStrategy;

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
        if (!targetNick.equals(nick) && !c.getModes().hasMode(Mode.OPERATOR)) {
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

            if (modeAction.length() != 2 || !Mode.userModes.containsKey(modeAction.substring(1))
            || (!modeAction.startsWith("+") && !modeAction.startsWith("-"))) {

                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, nick + " :Unknown umode flag"));
            } else {
                String operation = modeAction.substring(0, 1);
                String flag = modeAction.substring(1);

                Mode mode = Mode.userModes.get(flag);
                UserModeStrategy strategy = instance.getUserModeStrategy(mode);

                if (operation.equals("+")) {
                    strategy.addMode(c, target, mode);
                }
                else if (operation.equals("-")) {
                    strategy.removeMode(c, target, mode);
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


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public Boolean canExecuteUnregistered() {
        return false;
    }
}

package com.francisbailey.irc.commands;

import com.francisbailey.irc.*;

/**
 * Created by fbailey on 13/12/16.
 */
public class MODE implements Executable {


    /**
     * @TODO find a better way to instantiate these field variables
     */
    private ServerManager instance;
    private Connection c;
    private ClientMessage cm;



    public void execute(Connection c, ClientMessage cm, ServerManager instance) {

        this.c = c;
        this.cm = cm;
        this.instance = instance;


        // Check if nick matches user
        if (!c.getClientInfo().getNick().equals(cm.getParameter(0))) {
            c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_USERSDONTMATCH, ": Can't change mode for other users"));
        }
        else if (cm.getParameterCount() < 2) {
            this.sendUsermode(c, instance);
        }
        else {

            String mode = cm.getParameter(1);

            if (mode.length() != 2 || UserMode.modeStringMap.get(mode.substring(1)) == null || (!mode.startsWith("+") && !mode.startsWith("-"))) {
                c.send(new ServerMessage(instance.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, ": Unknown umode flag"));
            }
            else {
                String action = mode.substring(0, 1);
                String flag = mode.substring(1);

                if (action.equals("-")) {
                    this.handleRemoveMode(flag);
                }
                else if (action.equals("+")) {
                    this.handleAddMode(flag);
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
        String modeis = c.getClientInfo().getMode().toString();
        c.send(new ServerMessage(instance.getName(), ServerMessage.RPL_UMODEIS, nick + " :+" + modeis));
    }


    private void handleAddMode(String flag) {

        // IRC protocol states that modes o, O, a should not be set
        // via MODE command
        if (!flag.equals("o") && !flag.equals("O") && !flag.equals("a")) {
            UserMode.ModeType mode = UserMode.modeStringMap.get(flag);
            c.getClientInfo().getMode().addMode(mode);
        }
    }


    private void handleRemoveMode(String flag) {

        // Users should not be able to de-restrict themselves
        if (!flag.equals("r")) {
            UserMode.ModeType mode = UserMode.modeStringMap.get(flag);
            c.getClientInfo().getMode().unsetMode(mode);
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public Boolean canExecuteUnregistered() {
        return false;
    }
}

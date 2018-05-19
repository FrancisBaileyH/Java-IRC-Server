package com.francisbailey.irc.command.internal;

import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.strategy.UserModeStrategy;

/**
 * Created by fbailey on 07/05/17.
 */
public class USERMODE implements Executable {

    @Override
    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String targetNick = clientMessage.getParameter(0);
        String nick = connection.getClientInfo().getNick();

        Connection target = server.findConnectionByNick(targetNick);

        // Check if nick matches user
        if (!targetNick.equals(nick) && !connection.getModes().hasMode(Mode.OPERATOR)) {
              connection.send(ServerMessageBuilder
                  .from(server.getName())
                  .withReplyCode(ServerMessage.ERR_USERSDONTMATCH)
                  .andMessage(nick + " :Can't change mode for other users")
                  .build()
            );
        }
        else if (target == null) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHNICK)
                .andMessage(nick + " :No such user")
                .build()
            );
        }
        else if (clientMessage.getParameterCount() < 2) {
            this.sendUsermode(connection, target, server);
        }
        else {
            String modeAction = clientMessage.getParameter(1);

            if (modeAction.length() != 2 || !Mode.userModes.containsKey(modeAction.substring(1))
            || (!modeAction.startsWith("+") && !modeAction.startsWith("-"))) {
                connection.send(ServerMessageBuilder
                    .from(server.getName())
                    .withReplyCode(ServerMessage.ERR_UMODEUNKNOWNFLAG)
                    .andMessage(nick + " :Unknown umode flag")
                    .build()
                );
            } else {
                String operation = modeAction.substring(0, 1);
                String flag = modeAction.substring(1);

                Mode mode = Mode.userModes.get(flag);
                UserModeStrategy strategy = server.getUserModeStrategy(mode);

                if (operation.equals("+")) {
                    strategy.addMode(connection, target, mode);
                }
                else if (operation.equals("-")) {
                    strategy.removeMode(connection, target, mode);
                }

                this.sendUsermode(connection, target, server);
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

        c.send(ServerMessageBuilder
            .from(instance.getName())
            .withReplyCode(ServerMessage.RPL_UMODEIS)
            .andMessage(nick + " :+" + modes)
            .build()
        );
    }


    @Override
    public int getMinimumParams() {
        return 1;
    }


    @Override
    public boolean canExecuteUnregistered() {
        return false;
    }
}

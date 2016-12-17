package com.francisbailey;

/**
 * Created by fbailey on 04/11/16.
 */


import java.util.ArrayList;


public class CommandParser {


    /**
     * IRC messages come in the following format:
     * :<prefix> <command> <parameters...>
     *
     * The prefix is optional, but the command must always be present. All
     * message arguments are delimited by a single whitespace. The message
     * is terminated by an \r\n character series. So we need to strip off the \r
     * character, and split the message into its base components.
     *
     * @param commandMessage
     */
    public ClientMessage parse(String commandMessage) throws MissingCommandParametersException {

        String strippedCommand = this.stripCarriageReturn(commandMessage);
        String[] components = strippedCommand.split(" ");

        String command;
        String prefix = null;
        int paramIdx = 2;
        ArrayList<String> parameters = new ArrayList<>();


        // Guard for messages that are missing the minimum
        // number of parameters
        if (components.length < 1) {
            throw new MissingCommandParametersException();
        }

        // Check for ":" to determine if first component is a prefix
        if (components[0].substring(0, 1).equals(":")) {

            prefix = components[0];
            command = components[1];
        }
        else {
            command = components[0];
            paramIdx = 1;
        }

        for (int i = paramIdx; i < components.length; i++) {

            // if one of the parameters starts with ":" we know that
            // a string with spaces can proceed it;
            if (components[i].startsWith(":")) {
                parameters.add(joinParameterComponents(components, i));
                break;
            }

            parameters.add(components[i]);
        }

        command = command.toUpperCase();

        return new ClientMessage(command, commandMessage, parameters, prefix);
    }


    private String stripCarriageReturn(String command) {
        return command.substring(0, command.length());
    }

    private String joinParameterComponents(String[] components, int start) {

        String joined = components[start].substring(1);

        for (int i = start + 1; i < components.length; i++) {

            joined += " " + components[i];
        }

        return joined;
    }

}

package com.francisbailey;

public class Main {

    /**
     * Commands to support
     *
     * PING, PONG, USER, NICK, JOIN, PART, QUIT, PRIVMSG, WHOIS, WHO
     */


    /**
     * @TODO - SYNC PROBLEMS?!?!
     * @param args
     */
    public static void main(String[] args) {

        CommandParser parser = new CommandParser();
        // pass in config, which shows available commands
        CommandFactory cf = new CommandFactory();
        Config config = new Config();

        Server s = new Server(parser, cf, config, 6667);
        s.listen();
    }
}

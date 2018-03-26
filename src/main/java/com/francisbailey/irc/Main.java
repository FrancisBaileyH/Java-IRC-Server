package com.francisbailey.irc;

import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;

public class Main {


    /**
     * @param args
     */
    public static void main(String[] args) throws IOException, ConfigurationException {

        File f = new File("config.xml");

        XMLConfigurationReader xcr = new XMLConfigurationReader(f);
        Config config = new Config(xcr.getConfiguration());

        CommandParser parser = new CommandParser(config.serverName);
        CommandFactory cf = new CommandFactory();

        Server s = new Server(parser, cf, config, 6667);
        s.listen();
    }
}

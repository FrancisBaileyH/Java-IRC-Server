package com.francisbailey.irc;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;



public class Main {


    /**
     * @param args
     */
    public static void main(String[] args) throws ConfigurationException {

        File f = new File("config.xml");

        System.setProperty("log4j.configurationFile", "log4j.properties");

        Logger logger = LogManager.getLogger(Main.class);
        logger.info("Starting up server...");

        XMLConfigurationReader xcr = new XMLConfigurationReader(f);
        Config config = new XMLConfig(xcr.getConfiguration());

        CommandParser parser = new CommandParser(config.getServerName());
        CommandFactory cf = new CommandFactory();

        Server s = new Server(parser, cf, config, 6667);
        s.listen();
    }
}

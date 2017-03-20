package com.francisbailey;

import com.francisbailey.irc.Config;
import com.francisbailey.irc.XMLConfigurationReader;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 16/12/16.
 */
public class ConfigTest {

    private Config config;


    @org.junit.Before
    public void setUp() throws Exception {

        File f = new File("src/test/java/test-config.xml");
        XMLConfigurationReader xcr = new XMLConfigurationReader(f);
        this.config = new Config(xcr.getConfiguration());
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    /**
     * Assert that values loaded from our xmlconfigreader are properly
     * saved to our config object.
     */
    public void testConfigValuesLoaded() {

        assertEquals("irc.test.com", config.serverName);
        assertEquals("Welcome to irc.test.com", config.welcomeMessage);

        List<HierarchicalConfiguration> channels = config.channels;

        for (HierarchicalConfiguration chan: channels) {

            assertEquals("#general", chan.getString("name"));
            assertEquals("A place for general discussion", chan.getString("topic"));
            break;
        }


        List<HierarchicalConfiguration> operators = config.operators;

        for (HierarchicalConfiguration operator: operators) {
            assertEquals("francis", operator.getString("username"));
            assertEquals("Password123", operator.getString("password"));
            break;
        }

    }

}
package com.francisbailey.irc.command;

import com.francisbailey.irc.MockServerManager;
import com.francisbailey.irc.*;
import org.junit.Before;

import java.io.File;

/**
 * Created by fbailey on 07/05/17.
 */
public class CommandTest {


    protected MockServerManager sm;
    protected ChannelManager cm;
    protected CommandParser cp;

    @Before
    public void setUp() throws Exception {

        File f = new File("src/test/java/test-config.xml");
        XMLConfigurationReader xcr = new XMLConfigurationReader(f);
        Config config = new Config(xcr.getConfiguration());

        this.cm = new ChannelManager(config.channels);
        this.sm = new MockServerManager("mockserver", this.cm);
        this.cp = new CommandParser("mockserver");
    }

}

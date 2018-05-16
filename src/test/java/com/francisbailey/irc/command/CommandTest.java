package com.francisbailey.irc.command;

import com.francisbailey.irc.MockServerManager;
import com.francisbailey.irc.*;
import org.junit.Before;

import java.io.File;

/**
 * Created by fbailey on 07/05/17.
 */
public class CommandTest {


    protected MockServerManager serverManager;
    protected ChannelManager channelManager;
    protected CommandParser commandParser;

    @Before
    public void setUp() throws Exception {

        File f = new File("src/test/java/test-config.xml");
        XMLConfigurationReader xcr = new XMLConfigurationReader(f);
        Config config = new XMLConfig(xcr.getConfiguration());

        this.channelManager = new ChannelManager(config.getChannels());
        this.serverManager = new MockServerManager("mockserver", this.channelManager);
        this.commandParser = new CommandParser("mockserver");
    }

}

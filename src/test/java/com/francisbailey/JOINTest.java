package com.francisbailey;


import com.francisbailey.irc.*;
import com.francisbailey.irc.commands.JOIN;

import org.junit.Before;
import org.junit.Test;

import java.io.File;


import static org.junit.Assert.*;


/**
 * Created by fbailey on 05/05/17.
 */
public class JOINTest {

    private ServerManager sm;
    private ChannelManager cm;
    private CommandParser cp;

    @Before
    public void setUp() throws Exception {

        File f = new File("src/test/java/test-config.xml");
        XMLConfigurationReader xcr = new XMLConfigurationReader(f);
        Config config = new Config(xcr.getConfiguration());
        this.cm = new ChannelManager(config.channels);
        this.sm = new MockServerManager("mockserver", this.cm);
        this.cp = new CommandParser();
    }


    @Test
    public void testJoinNonExistentChannel() throws MissingCommandParametersException {

        Executable exe = new JOIN();
        MockConnection c = MockRegisteredConnectionFactory.build();

        ClientMessage cm = this.cp.parse("JOIN #nochannel");

        exe.execute(c, cm, this.sm);

        ServerMessage expected = new ServerMessage(this.sm.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick() + " #nochannel :No such channel");
        assertEquals("Incorrect non-existent channel message", c.getLastOutput(), expected.compile());
    }


    @Test
    public void testJoinChannel() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();
        MockConnection joiner = MockRegisteredConnectionFactory.build();

        this.cm.getChannel("#general").join(userA);
        this.cm.getChannel("#general").join(userB);

        ClientMessage cm = this.cp.parse("JOIN #general");
        Executable exe = new JOIN();
        exe.execute(joiner, cm, this.sm);

        ServerMessage expected = new ServerMessage(joiner.getClientInfo().getHostmask(), ServerMessage.RPL_JOIN, "#general");

        assertEquals("Channel user didn't receive join", userA.getLastOutput(), expected.compile());
        assertEquals("Channel user didn't receive join", userB.getLastOutput(), expected.compile());
    }

}

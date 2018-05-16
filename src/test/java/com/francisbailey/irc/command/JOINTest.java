package com.francisbailey.irc.command;


import com.francisbailey.irc.MockConnection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;

import com.francisbailey.irc.exception.MissingCommandParametersException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by fbailey on 05/05/17.
 */
public class JOINTest extends CommandTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Assert that a user can't join a non-existent channel
     * @throws MissingCommandParametersException
     */
    @Test
    public void testJoinNonExistentChannel() throws MissingCommandParametersException {

        Executable exe = new JOIN();
        MockConnection c = MockRegisteredConnectionFactory.build();

        ClientMessage cm = this.commandParser.parse("JOIN #nochannel");

        exe.execute(c, cm, this.serverManager);

        ServerMessage expected = new ServerMessage(this.serverManager.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick() + " #nochannel :No such channel");
        assertEquals("Incorrect non-existent channel message", c.getLastOutput().compile(), expected.compile());
    }


    /**
     * @TODO - assert that NAMES list and channel topic are sent to joiner
     * Assert that when a user joins a channel other users are notifed
     * @throws MissingCommandParametersException
     */
    @Test
    public void testJoinChannel() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();
        MockConnection joiner = MockRegisteredConnectionFactory.build();

        this.channelManager.getChannel("#general").addUser(userA);
        this.channelManager.getChannel("#general").addUser(userB);

        ClientMessage cm = this.commandParser.parse("JOIN #general");
        Executable exe = new JOIN();
        exe.execute(joiner, cm, this.serverManager);

        ServerMessage expected = new ServerMessage(joiner.getClientInfo().getHostmask(), ServerMessage.RPL_JOIN, "#general");

        assertEquals("Channel user didn't receive join", userA.getLastOutput().compile(), expected.compile());
        assertEquals("Channel user didn't receive join", userB.getLastOutput().compile(), expected.compile());
    }

}

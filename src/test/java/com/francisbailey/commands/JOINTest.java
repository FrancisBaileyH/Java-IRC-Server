package com.francisbailey.commands;


import com.francisbailey.MockConnection;
import com.francisbailey.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;
import com.francisbailey.irc.commands.JOIN;

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

        ClientMessage cm = this.cp.parse("JOIN #nochannel");

        exe.execute(c, cm, this.sm);

        ServerMessage expected = new ServerMessage(this.sm.getName(), ServerMessage.ERR_NOSUCHCHANNEL, c.getClientInfo().getNick() + " #nochannel :No such channel");
        assertEquals("Incorrect non-existent channel message", c.getLastOutput(), expected.compile());
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

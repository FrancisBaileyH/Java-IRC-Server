package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.exception.IRCActionException;
import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by fbailey on 28/03/18.
 */
public class ChannelModeArgStrategyTest extends ModeStrategyTest {


    private ChannelModeArgStrategy strategy;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.strategy = new ChannelModeArgStrategy(this.sm);
    }

    @Test
    public void testAddBadUserLimit() throws Exception {

        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        try {
            this.strategy.addMode(chan, c, Mode.USER_LIMIT, "bad int");
            fail("Exception not thrown");
        }
        catch (IRCActionException e) {
            assertEquals(ServerMessage.ERR_BADMASK, e.getReplyCode());
        }
    }

    @Test
    public void testRemoveBadUserLimit() throws Exception {
        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        try {
            this.strategy.removeMode(chan, c, Mode.USER_LIMIT, "bad int");
            fail("Exception not thrown");
        }
        catch (IRCActionException e) {
            assertEquals(ServerMessage.ERR_BADMASK, e.getReplyCode());
        }
    }

    @Test
    public void testSetChannelKey() throws Exception {
        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        this.strategy.addMode(chan, c, Mode.CHAN_KEY, "foobar");
        assertTrue(chan.getKey().equals("foobar"));

        this.strategy.removeMode(chan, c, Mode.CHAN_KEY, null);
        assertNull(chan.getKey());
    }


    @Test
    public void testSetUserLimit() throws Exception {
        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        this.strategy.addMode(chan, c, Mode.USER_LIMIT, "10");
        assertEquals(chan.getUserLimit(), 10);
    }



}

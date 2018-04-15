package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 28/03/18.
 */
public class StandardChannelModeStrategyTest extends ModeStrategyTest {

    private StandardChannelModeStrategy strategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.strategy = new StandardChannelModeStrategy(this.sm);
    }

    @Test
    public void setMode() throws Exception {

        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "mock topic");

        this.strategy.addMode(chan, c, Mode.MODERATED, null);
        assertTrue(chan.hasMode(Mode.MODERATED));

        this.strategy.removeMode(chan, c, Mode.MODERATED, null);
        assertFalse(chan.hasMode(Mode.MODERATED));
    }

}
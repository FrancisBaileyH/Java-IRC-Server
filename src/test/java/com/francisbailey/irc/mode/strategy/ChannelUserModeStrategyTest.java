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
public class ChannelUserModeStrategyTest extends ModeStrategyTest {

    private ChannelUserModeStrategy strategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.strategy = new ChannelUserModeStrategy(this.sm);
    }

    @Test
    public void testSetModeForUserNotOnChannel() throws Exception {
        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        try {
            this.strategy.addMode(chan, c, Mode.CHAN_OPERATOR, "");
            fail("Exception not thrown");
        } catch (IRCActionException e) {
            assertEquals(e.getReplyCode(), ServerMessage.ERR_USERNOTINCHANNEL);
        }

        try {
            this.strategy.removeMode(chan, c, Mode.CHAN_OPERATOR, "");
            fail("Exception not thrown");
        } catch (IRCActionException e) {
            assertEquals(e.getReplyCode(), ServerMessage.ERR_USERNOTINCHANNEL);
        }
    }

    @Test
    public void testOwnerModeCantBeChanged() throws Exception {
        Connection owner = MockRegisteredConnectionFactory.build();
        Connection badUser = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        chan.addUser(owner);
        chan.addUser(badUser);
        chan.addModeForUser(owner, Mode.OWNER);

        try {
            this.strategy.removeMode(chan, badUser, Mode.OWNER, owner.getClientInfo().getNick());
            fail("Exception not thrown");
        } catch (IRCActionException e) {
            assertEquals(e.getReplyCode(), ServerMessage.ERR_NOPRIVILEGES);
        }
    }

    @Test
    public void testSetMode() throws Exception {
        Connection c = MockRegisteredConnectionFactory.build();
        Channel chan = new Channel("#foobar", "test");

        chan.addUser(c);

        this.strategy.addMode(chan, c, Mode.CHAN_OPERATOR, c.getClientInfo().getNick());
        assertTrue(chan.hasModeForUser(c, Mode.CHAN_OPERATOR));

        this.strategy.removeMode(chan, c, Mode.CHAN_OPERATOR, c.getClientInfo().getNick());
        assertFalse(chan.hasModeForUser(c, Mode.CHAN_OPERATOR));
    }

}

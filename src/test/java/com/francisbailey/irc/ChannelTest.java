package com.francisbailey.irc;

import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 10/05/17.
 */
public class ChannelTest {


    private Channel chan;
    private String topic = "Test Topic";
    private String name = "#general";


    @Before
    public void setUp() {
        this.chan = new Channel(this.name, this.topic);
    }




    @Test
    public void testChangeModeForUser() {
        Connection c = MockRegisteredConnectionFactory.build();
        this.chan.addUser(c);

        Mode mode = new Mode("f", "MOCK_MODE");

        this.chan.addModeForUser(c, mode);
        assertTrue(this.chan.hasModeForUser(c, mode));
        assertEquals(this.chan.getModesForUser(c).toString(), mode.getFlag());

        this.chan.removeModeForUser(c, mode);
        assertFalse(this.chan.hasModeForUser(c, mode));
        assertEquals(this.chan.getModesForUser(c).toString(), "");
    }



    @Test
    public void testChangeChannelMode() {
        this.chan.addMode(Mode.OP_TOPIC_ONLY);
        assertTrue(this.chan.hasMode(Mode.OP_TOPIC_ONLY));

        this.chan.removeMode(Mode.OP_TOPIC_ONLY);
        assertFalse(this.chan.hasMode(Mode.OP_TOPIC_ONLY));
    }


    @Test
    public void testChangeChannelMask() {
        String mask = "!@foobar";

        this.chan.addMask(Mode.BAN_MASK, mask);
        assertTrue(this.chan.getMask(Mode.BAN_MASK).next().toString().equals(mask));

        this.chan.removeMask(Mode.BAN_MASK, mask);
        assertNull(this.chan.getMask(Mode.BAN_MASK));
    }


    @Test
    public void testFindConnectionByNick() {

        Connection userA = MockRegisteredConnectionFactory.build();
        Connection userB = MockRegisteredConnectionFactory.build();
        Connection userC = MockRegisteredConnectionFactory.build();

        this.chan.addUser(userA);
        this.chan.addUser(userB);
        this.chan.addUser(userC);

        String searchNick = userB.getClientInfo().getNick();

        Connection existentUser = this.chan.findConnectionByNick(searchNick);
        assertTrue(existentUser.getClientInfo().getNick().equals(searchNick));

        this.chan.removeUser(userB);

        Connection nonexistentUser = this.chan.findConnectionByNick(searchNick);
        assertNull(nonexistentUser);
    }

}

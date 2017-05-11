package com.francisbailey;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.ServerMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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


    /**
     * Assert that users are notified when a new user joins
     * and that the new user is properly added to the channel.
     */
    @Test
    public void testAddChannelUser() {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();

        this.chan.join(userA);
        this.chan.join(userB);

        ServerMessage expected = new ServerMessage(userB.getClientInfo().getHostmask(), ServerMessage.RPL_JOIN, this.name);

        assertTrue(this.chan.hasUser(userA));
        assertTrue(this.chan.hasUser(userB));

        assertEquals(expected.compile(), userA.getLastOutput());
    }


    /**
     * Assert that users are properly notified when someone leaves the
     * channel and that the parting user is properly removed from
     * the channel.
     */
    @Test
    public void testRemoveChannelUser() {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();

        this.chan.join(userA);
        this.chan.join(userB);

        this.chan.part(userA, "leaving");

        ServerMessage expected = new ServerMessage(userA.getClientInfo().getHostmask(), ServerMessage.RPL_PART, this.chan.getName() + " :leaving");
        assertEquals(userB.getLastOutput(), expected.compile());

        this.chan.part(userB, "leaving");

        assertFalse(this.chan.hasUser(userA));
        assertFalse(this.chan.hasUser(userB));
    }


    @Test
    public void testChangeChannelMode() {

    }


    @Test
    public void testChangeChannelTopic() {


    }


    @Test
    public void testInviteOnlyChannel() {

    }


}

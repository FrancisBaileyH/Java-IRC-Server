package com.francisbailey.irc.commands;

import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.Channel;
import com.francisbailey.irc.ClientMessage;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.commands.internal.CHANMODE;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 30/05/17.
 */
public class CHANMODETest extends CommandTest {

    private final String channelName = "#foo";
    private final Connection chanOp = MockRegisteredConnectionFactory.build();
    private final Connection chanUser = MockRegisteredConnectionFactory.build();
    private Channel testChannel;


    @Before
    public void setUp() throws Exception {

        super.setUp();
        this.sm.getChannelManager().addChannel(channelName, "");
        this.testChannel = this.sm.getChannelManager().getChannel(channelName);
    }


    /**
     * Assert that non operators can not change modes
     * on a channel
     */
    @Test
    public void testNonOPChangeModes() throws Exception {

        CHANMODE exe = new CHANMODE();
        this.testChannel.getModes().addMode(this.testChannel, "i");

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +t");
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -i");

        exe.execute(this.chanUser, cmA, this.sm);
        assertFalse(this.testChannel.getModes().hasMode(this.testChannel, "t"));

        exe.execute(this.chanUser, cmB, this.sm);
        assertTrue(this.testChannel.getModes().hasMode(this.testChannel, "i"));
    }


    /**
     * Assert that modes are properly set on a
     * given user within a channel
     */
    @Test
    public void testSetChannelUserModes() throws Exception {

        CHANMODE exe = new CHANMODE();
        String chanUserNick = this.chanUser.getClientInfo().getNick();

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +v " + chanUserNick);
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -v " + chanUserNick);
        ClientMessage cmC = this.cp.parse("MODE " + this.channelName + " +v " + "nonExistantUser");

        exe.execute(chanOp, cmA, this.sm);
        assertTrue(this.chanUser.getModes().hasMode(this.testChannel, "v"));

        exe.execute(chanOp, cmB, this.sm);
        assertFalse(this.chanUser.getModes().hasMode(this.testChannel, "v"));

        // @TODO assert user not on channel doesn't have mode added
    }


    /**
     * Assert that channel modes can be set
     */
    @Test
    public void testSetChannelModes() throws Exception {

        CHANMODE exe = new CHANMODE();

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +m");
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -m");

        exe.execute(chanOp, cmA, this.sm);
        assertTrue(this.testChannel.getModes().hasMode(this.testChannel, "m"));

        exe.execute(chanOp, cmB, this.sm);
        assertFalse(this.testChannel.getModes().hasMode(this.testChannel, "m"));
    }


    /**
     * Assert that channel key is set/removed properly
     */
    @Test
    public void testChangeChannelKey() throws Exception{

        CHANMODE exe = new CHANMODE();
        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +k fookey");
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -k fookey");

        exe.execute(chanOp, cmA, this.sm);
        assertEquals(this.testChannel.getKey(), "fookey");

        exe.execute(chanOp, cmB, this.sm);
        assertNull(this.testChannel.getKey());
    }


    /**
     * Assert that channel limit is set/removed properly
     */
    @Test
    public void testChangeChannelLimit() {

    }


    /**
     * @TODO for now we'll leave unimplemented until
     * a system for banning is in place
     * Assert that channel has ban mask set/removed properly
      */
    @Test
    public void testChangeBanMask() {

    }


    /**
     * Assert that channel has exception mask set/removed properly
     */
    @Test
    public void testChangeExceptionMask() {

    }


    /**
     * Assert that channel has invitation mask set/removed properly
     */
    @Test
    public void testChangeInvitationMask() {

    }


}



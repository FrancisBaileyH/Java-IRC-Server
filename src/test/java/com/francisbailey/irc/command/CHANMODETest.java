package com.francisbailey.irc.command;

import com.francisbailey.irc.*;
import com.francisbailey.irc.command.internal.CHANMODE;
import com.francisbailey.irc.mode.Mode;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 30/05/17.
 */
public class CHANMODETest extends CommandTest {

    private final String channelName = "#foo";
    private final MockConnection chanOp = MockRegisteredConnectionFactory.build();
    private final MockConnection chanUser = MockRegisteredConnectionFactory.build();
    private Channel testChannel;


    @Before
    public void setUp() throws Exception {

        super.setUp();
        this.sm.getChannelManager().addChannel(channelName, "");
        this.testChannel = this.sm.getChannelManager().getChannel(channelName);
        this.testChannel.addUser(this.chanOp);
        this.testChannel.addUser(this.chanUser);
        this.testChannel.addModeForUser(this.chanOp, Mode.CHAN_OPERATOR);
    }


    /**
     * Assert that non operators can not change mode
     * on a channel
     */
    @Test
    public void testNonOPChangeModes() throws Exception {

        CHANMODE exe = new CHANMODE();

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +a");
        exe.execute(this.chanUser, cmA, this.sm);

        MockChannelModeStrategy ms = (MockChannelModeStrategy)this.sm.getChannelModeStrategy(null);
        ServerMessage result = this.chanUser.getLastOutput();

        assertEquals(result.getServerReply(), ServerMessage.ERR_CHANOPRIVSNEEDED);
        assertEquals(ms.getLastModeSet(), null);
    }


    /**
     * Assert that mode are properly set on a
     * given user within a channel
     */
    @Test
    public void testSetModes() throws Exception {

        CHANMODE exe = new CHANMODE();
        String chanUserNick = this.chanUser.getClientInfo().getNick();

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +v " + chanUserNick);
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -v " + chanUserNick);

        MockChannelModeStrategy ms = (MockChannelModeStrategy)this.sm.getChannelModeStrategy(null);

        exe.execute(this.chanOp, cmA, this.sm);
        assertEquals(ms.getLastModeSet(), Mode.VOICE);
        assertEquals(ms.getLastArg(), chanUserNick);
        assertEquals(ms.getLastOp(), MockChannelModeStrategy.OPERATION.ADD);

        exe.execute(this.chanOp, cmB, this.sm);
        assertEquals(ms.getLastModeSet(), Mode.VOICE);
        assertEquals(ms.getLastArg(), chanUserNick);
        assertEquals(ms.getLastOp(), MockChannelModeStrategy.OPERATION.REMOVE);
    }


    /**
     * Assert that channel mode can be set
     */
    @Test
    public void testSetChannelModes() throws Exception {

        CHANMODE exe = new CHANMODE();

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +m");
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -m");

        exe.execute(this.chanOp, cmA, this.sm);
        assertTrue(testChannel.hasMode(Mode.MODERATED));

        exe.execute(this.chanOp, cmB, this.sm);
        assertFalse(testChannel.hasMode(Mode.MODERATED));
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
     * Assert that a channel key is set properly
     */
    @Test
    public void testChangeChannelLimit() throws Exception {

        CHANMODE exe = new CHANMODE();
        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +l 10");

        exe.execute(chanOp, cmA, this.sm);
        assertEquals(this.testChannel.getUserLimit(), 10);
    }


    @Test
    public void testChangeBanMask() throws Exception {
        CHANMODE exe = new CHANMODE();
        String mask = "!abc123";

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +b " + mask);
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -b " + mask);

        exe.execute(this.chanOp, cmA, this.sm);

        Pattern pattern = testChannel.getMask(Mode.BAN_MASK).next();
        assertEquals(mask, pattern.toString());

        exe.execute(this.chanOp, cmB, this.sm);

        System.out.println(testChannel.getMask(Mode.BAN_MASK).next());
        assertNull(testChannel.getMask(Mode.BAN_MASK));
    }


    /**
     * Assert that channel has exception mask set/removed properly
     */
    @Test
    public void testChangeExceptionMask() throws Exception {
        CHANMODE exe = new CHANMODE();
        String mask = "!abc123";

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +e " + mask);
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -e " + mask);

        exe.execute(this.chanOp, cmA, this.sm);

        Pattern pattern = testChannel.getMask(Mode.BAN_MASK_EXCEPTION).next();
        assertEquals(mask, pattern.toString());

        exe.execute(this.chanOp, cmB, this.sm);

        System.out.println(testChannel.getMask(Mode.BAN_MASK_EXCEPTION).next());
        assertNull(testChannel.getMask(Mode.BAN_MASK_EXCEPTION));
        assertNull(testChannel.getMask(Mode.BAN_MASK_EXCEPTION));
    }


    /**
     * Assert that channel has invitation mask set/removed properly
     */
    @Test
    public void testChangeInvitationMask() throws Exception {
        CHANMODE exe = new CHANMODE();
        String mask = "!abc123";

        ClientMessage cmA = this.cp.parse("MODE " + this.channelName + " +I " + mask);
        ClientMessage cmB = this.cp.parse("MODE " + this.channelName + " -I " + mask);

        exe.execute(this.chanOp, cmA, this.sm);

        Pattern pattern = testChannel.getMask(Mode.INVITATION_MASK).next();
        assertEquals(mask, pattern.toString());

        exe.execute(this.chanOp, cmB, this.sm);

        System.out.println(testChannel.getMask(Mode.INVITATION_MASK).next());
        assertNull(testChannel.getMask(Mode.INVITATION_MASK));
        assertNull(testChannel.getMask(Mode.INVITATION_MASK));
    }
}



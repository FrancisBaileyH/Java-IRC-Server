package com.francisbailey.irc.command;

import com.francisbailey.irc.*;
import com.francisbailey.irc.command.internal.CHANMODE;
import com.francisbailey.irc.mode.Mode;

import org.junit.Before;
import org.junit.Test;


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
        this.serverManager.getChannelManager().addChannel(channelName, "");
        this.testChannel = this.serverManager.getChannelManager().getChannel(channelName);
        this.testChannel.addUser(this.chanOp);
        this.testChannel.addUser(this.chanUser);
        this.testChannel.addModeForUser(this.chanOp, Mode.CHAN_OPERATOR);
    }

    @Test
    public void testNonOPChangeModes() throws Exception {

        CHANMODE exe = new CHANMODE();

        ClientMessage cmA = this.commandParser.parse("MODE " + this.channelName + " +a");
        exe.execute(this.chanUser, cmA, this.serverManager);

        MockChannelModeStrategy ms = (MockChannelModeStrategy)this.serverManager.getChannelModeStrategy(null);
        ServerMessage result = this.chanUser.getLastOutput();

        assertEquals(result.getServerReply(), ServerMessage.ERR_CHANOPRIVSNEEDED);
        assertEquals(ms.getLastModeSet(), null);
    }

    @Test
    public void testSetModes() throws Exception {

        CHANMODE exe = new CHANMODE();
        String chanUserNick = this.chanUser.getClientInfo().getNick();

        ClientMessage cmA = this.commandParser.parse("MODE " + this.channelName + " +v " + chanUserNick);
        ClientMessage cmB = this.commandParser.parse("MODE " + this.channelName + " -v " + chanUserNick);

        MockChannelModeStrategy ms = (MockChannelModeStrategy)this.serverManager.getChannelModeStrategy(null);

        exe.execute(this.chanOp, cmA, this.serverManager);
        assertEquals(ms.getLastModeSet(), Mode.VOICE);
        assertEquals(ms.getLastArg(), chanUserNick);
        assertEquals(ms.getLastOp(), MockChannelModeStrategy.OPERATION.ADD);

        exe.execute(this.chanOp, cmB, this.serverManager);
        assertEquals(ms.getLastModeSet(), Mode.VOICE);
        assertEquals(ms.getLastArg(), chanUserNick);
        assertEquals(ms.getLastOp(), MockChannelModeStrategy.OPERATION.REMOVE);
    }

    @Test
    public void testNoChannelModes() throws Exception {
        CHANMODE exe = new CHANMODE();
        MockConnection c = MockRegisteredConnectionFactory.build();

        Channel chan = new Channel("#foobar", "test");
        this.serverManager.getChannelManager().addChannel(chan);

        chan.addUser(c);
        chan.addModeForUser(c, Mode.OWNER);

        ClientMessage cm = this.commandParser.parse("MODE " + chan.getName());
        exe.execute(c, cm, this.serverManager);

        assertEquals(c.getLastOutput().getServerReply(), ServerMessage.ERR_NOCHANMODES);
    }

    @Test
    public void testNoSuchChannel() throws Exception {
        CHANMODE exe = new CHANMODE();
        MockConnection c = MockRegisteredConnectionFactory.build();

        ClientMessage cm = this.commandParser.parse("MODE " + "#nosuchchannel");
        exe.execute(c, cm, this.serverManager);

        assertEquals(c.getLastOutput().getServerReply(), ServerMessage.ERR_NOSUCHCHANNEL);
    }

    @Test
    public void testListChannelModes() throws Exception {
        CHANMODE exe = new CHANMODE();

        this.testChannel.addMode(Mode.MODERATED);
        this.testChannel.addMode(Mode.OP_TOPIC_ONLY);

        ClientMessage cm = this.commandParser.parse("MODE " + this.testChannel.getName());
        exe.execute(this.chanUser, cm, this.serverManager);

        ServerMessage expected = new ServerMessage(this.serverManager.getName(), ServerMessage.RPL_CHANNELMODEIS, this.chanUser.getClientInfo().getNick() + " " + this.testChannel.getName() + " +mt");

        assertEquals(this.chanUser.getLastOutput().compile(), expected.compile());
    }

    @Test
    public void testNoSuchMode() throws Exception {
        CHANMODE exe = new CHANMODE();

        Mode badMode = new Mode("A", "Bad Mode");
        ClientMessage cm = this.commandParser.parse("MODE " + this.testChannel.getName() + " +" + badMode.getFlag());

        exe.execute(this.chanOp, cm, this.serverManager);
        assertEquals(this.chanOp.getLastOutput().getServerReply(), ServerMessage.ERR_UNKNOWNMODE);
    }

    @Test
    public void testNeedMoreParams() throws Exception {
        CHANMODE exe = new CHANMODE();

        // Test mode that requires an arg
        String modeThatRequiresArg = Mode.BAN_MASK.getFlag();

        ClientMessage cm = this.commandParser.parse("MODE " + this.testChannel.getName() + " +t" + modeThatRequiresArg);

        exe.execute(this.chanOp, cm, this.serverManager);
        assertEquals(this.chanOp.getLastOutput().getServerReply(), ServerMessage.ERR_NEEDMOREPARAMS);
    }

}



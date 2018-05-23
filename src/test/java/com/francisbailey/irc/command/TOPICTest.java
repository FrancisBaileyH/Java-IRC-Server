package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.MockConnection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TOPICTest extends CommandTest {


    private Channel testChannel;


    @Before
    public void setUp() throws Exception {
        super.setUp();

        String channelName = "#testchannel";

        this.serverManager.getChannelManager().removeChannel(channelName);
        this.testChannel = new Channel(channelName, "test topic");
        this.serverManager.getChannelManager().addChannel(this.testChannel);
        this.testChannel.clearModes();
    }


    @Test
    public void testSecretChannelIgnored() throws Exception {
        MockConnection connection = MockRegisteredConnectionFactory.build();
        this.testChannel.addUser(connection);
        this.testChannel.addMode(Mode.SECRET);

        Executable exe = new TOPIC();
        ClientMessage clientMessage = this.commandParser.parse("TOPIC " + this.testChannel.getName());

        exe.execute(connection, clientMessage, this.serverManager);

        assertNull(connection.getLastOutput());
    }


    @Test
    public void testPrivateChannelIgnored() throws Exception {
        MockConnection connection = MockRegisteredConnectionFactory.build();
        this.testChannel.addUser(connection);
        this.testChannel.addMode(Mode.PRIVATE);

        Executable exe = new TOPIC();
        ClientMessage clientMessage = this.commandParser.parse("TOPIC " + this.testChannel.getName());

        exe.execute(connection, clientMessage, this.serverManager);

        assertNull(connection.getLastOutput());
    }


    @Test
    public void testNonExistentChannelError() throws Exception {
        MockConnection connection = MockRegisteredConnectionFactory.build();

        Executable exe = new TOPIC();
        ClientMessage clientMessage = this.commandParser.parse("TOPIC #badChannel");

        exe.execute(connection, clientMessage, this.serverManager);

        assertEquals(connection.getLastOutput().getServerReply(), ServerMessage.ERR_NOSUCHCHANNEL);
    }


    @Test
    public void testNotOnChannelError() throws Exception {
        MockConnection connection = MockRegisteredConnectionFactory.build();

        Executable exe = new TOPIC();
        ClientMessage clientMessage = this.commandParser.parse("TOPIC " + this.testChannel.getName());

        exe.execute(connection, clientMessage, this.serverManager);

        assertEquals(connection.getLastOutput().getServerReply(), ServerMessage.ERR_NOTONCHANNEL);
    }


    @Test
    public void testChannelWithTopicListing() throws Exception {
        String newTopic = "Foobar Topic";

        MockConnection connectionA = MockRegisteredConnectionFactory.build();
        MockConnection connectionB = MockRegisteredConnectionFactory.build();

        this.testChannel.addUser(connectionA);
        this.testChannel.addUser(connectionB);

        Executable exe = new TOPIC();
        ClientMessage clientMessage = this.commandParser.parse("TOPIC " + this.testChannel.getName() + " :" + newTopic);

        exe.execute(connectionA, clientMessage, this.serverManager);

        assertEquals(this.testChannel.getTopic(), newTopic);
        assertEquals(connectionA.getLastOutput().getServerReply(), ServerMessage.RPL_TOPIC_CHANGE);
        assertEquals(connectionB.getLastOutput().getServerReply(), ServerMessage.RPL_TOPIC_CHANGE);
        assertEquals(connectionB.getLastOutput().getOrigin(), connectionA.getClientInfo().getHostmask());
    }


    @Test
    public void testChannelWithoutTopicListing() throws Exception {
         MockConnection connectionA = MockRegisteredConnectionFactory.build();
        MockConnection connectionB = MockRegisteredConnectionFactory.build();

        this.testChannel.addUser(connectionA);
        this.testChannel.addUser(connectionB);

        Executable exe = new TOPIC();
        ClientMessage clientMessage = this.commandParser.parse("TOPIC " + this.testChannel.getName() + " :");

        exe.execute(connectionA, clientMessage, this.serverManager);

        assertEquals(this.testChannel.getTopic(), "");
        assertEquals(connectionA.getLastOutput().getServerReply(), ServerMessage.RPL_TOPIC_CHANGE);
        assertEquals(connectionB.getLastOutput().getServerReply(), ServerMessage.RPL_TOPIC_CHANGE);
        assertEquals(connectionB.getLastOutput().getOrigin(), connectionA.getClientInfo().getHostmask());
    }
}

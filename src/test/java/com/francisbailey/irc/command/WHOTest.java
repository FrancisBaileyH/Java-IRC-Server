package com.francisbailey.irc.command;


import com.francisbailey.irc.MockConnection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;
import com.francisbailey.irc.exception.MissingCommandParametersException;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by fbailey on 10/05/17.
 */
public class WHOTest extends CommandTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.serverManager.getChannelManager().addChannel("#foo", "Test Topic");
    }


    /**
     * Assert that users with the invisible mode are not
     * listed from WHO
     * @throws MissingCommandParametersException
     */
    @Test
    public void testWhoListInvisibleUser() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();
        MockConnection userC = MockRegisteredConnectionFactory.build();
        MockConnection userD = MockRegisteredConnectionFactory.build();

        Channel c = this.serverManager.getChannelManager().getChannel("#foo");
        c.addUser(userA);
        c.addUser(userB);
        c.addUser(userC);
        c.addUser(userD);

        userD.getModes().addMode(Mode.INVISIBLE);

        userA.clearOutputBuffer();

        ClientMessage cm = this.commandParser.parse("WHO #foo");
        WHO exe = new WHO();
        exe.execute(userA, cm, this.serverManager);

        ServerMessage sm1 = buildExpectedWhoListMessage(userA, c);
        ServerMessage sm2 = buildExpectedWhoListMessage(userB, c);
        ServerMessage sm3 = buildExpectedWhoListMessage(userC, c);
        ServerMessage sm4 = new ServerMessage(this.serverManager.getName(), ServerMessage.RPL_ENDOFWHO);

        String expected = sm1.compile() + sm2.compile() + sm3.compile() + sm4.compile();

        ArrayList<ServerMessage> messages = userA.getAllOutput();

        String output = "";

        for (ServerMessage serverMessage: messages) {
            output += serverMessage.compile();
        }

        assertEquals(output, expected);
    }


    private ServerMessage buildExpectedWhoListMessage(Connection c, Channel ch) {

        Client ci = c.getClientInfo();
        String message = ci.getNick();
        message += " " + ch.getName();
        message += " " + ci.getUsername();
        message += " " + ci.getHostname();
        message += " " + this.serverManager.getName();
        message += " " + ci.getNick();
        message += " :0 " + ci.getRealname();
        return new ServerMessage(this.serverManager.getName(), ServerMessage.RPL_WHOREPLY, message);
    }

}

package com.francisbailey.commands;


import com.francisbailey.MockConnection;
import com.francisbailey.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;
import com.francisbailey.irc.commands.WHO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by fbailey on 10/05/17.
 */
public class WHOTest extends CommandTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.sm.getChannelManager().addChannel("#foo", "Test Topic");
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

        Channel c = this.sm.getChannelManager().getChannel("#foo");
        c.join(userA);
        c.join(userB);
        c.join(userC);
        c.join(userD);

        userD.getModes().addMode("i");
        userA.clearOutputBuffer();

        ClientMessage cm = this.cp.parse("WHO #foo");
        WHO exe = new WHO();
        exe.execute(userA, cm, this.sm);

        ServerMessage sm1 = buildExpectedWhoListMessage(userA, c);
        ServerMessage sm2 = buildExpectedWhoListMessage(userB, c);
        ServerMessage sm3 = buildExpectedWhoListMessage(userC, c);
        ServerMessage sm4 = new ServerMessage(this.sm.getName(), ServerMessage.RPL_ENDOFWHO);

        String expected = sm1.compile() + sm2.compile() + sm3.compile() + sm4.compile();
        assertEquals(userA.getAllOutput(), expected);
    }


    private ServerMessage buildExpectedWhoListMessage(Connection c, Channel ch) {

        Client ci = c.getClientInfo();
        String message = ci.getNick();
        message += " " + ch.getName();
        message += " " + ci.getUsername();
        message += " " + ci.getHostname();
        message += " " + this.sm.getName();
        message += " " + ci.getNick();
        message += " :0 " + ci.getRealname();
        return new ServerMessage(this.sm.getName(), ServerMessage.RPL_WHOREPLY, message);
    }

}

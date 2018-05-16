package com.francisbailey.irc.command;

import com.francisbailey.irc.MockConnection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;
import com.francisbailey.irc.command.internal.USERMODE;
import com.francisbailey.irc.exception.MissingCommandParametersException;
import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by fbailey on 07/05/17.
 */
public class USERMODETest extends CommandTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Assert that users can not set the operator mode on
     * themselves
     * @throws MissingCommandParametersException
     */
    @Test
    public void testOperatorModeDenied() throws MissingCommandParametersException {

        Connection c = MockRegisteredConnectionFactory.build();
        String nick = c.getClientInfo().getNick();

        ClientMessage cmA = this.commandParser.parse("MODE " + nick + " +O");
        ClientMessage cmB = this.commandParser.parse("MODE " + nick + " +o");
        ClientMessage cmC = this.commandParser.parse("MODE " + nick + " +a");

        Executable exe = new USERMODE();
        exe.execute(c, cmA, this.serverManager);
        exe.execute(c, cmB, this.serverManager);
        exe.execute(c, cmC, this.serverManager);

        assertFalse(c.getModes().hasMode(Mode.LOCAL_OPERATOR));
        assertFalse(c.getModes().hasMode(Mode.OPERATOR));
        assertFalse(c.getModes().hasMode(Mode.AWAY));
    }


    /**
     * Assert that users can not set unknown mode types
     * @throws MissingCommandParametersException
     */
    @Test
    public void testNonexistentMode() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        this.serverManager.setFindNickValue(userA);

        String userANick = userA.getClientInfo().getNick();

        ClientMessage cm = this.commandParser.parse("MODE " + userANick + " +g");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.serverManager);

        ServerMessage expected = new ServerMessage(this.serverManager.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, userANick + " :Unknown umode flag");
        assertEquals(expected.getServerReply(), userA.getLastOutput().getServerReply());
        assertFalse(userA.getModes().hasMode(new Mode("g", "UNKNOWN MODE")));
    }



    /**
     * Assert that users can not set mode for another user
     * @throws MissingCommandParametersException
     */
    @Test
    public void testModeSetOtherUser() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();

        String userBNick = userB.getClientInfo().getNick();
        this.serverManager.setFindNickValue(userB);

        ClientMessage cm = this.commandParser.parse("MODE " + userBNick + " +i");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.serverManager);

        ServerMessage expected = new ServerMessage(this.serverManager.getName(), ServerMessage.ERR_USERSDONTMATCH, userA.getClientInfo().getNick() +  " :Can't change mode for other users");

        assertEquals(expected.getServerReply(), userA.getLastOutput().getServerReply());
        assertFalse(userB.getModes().hasMode(Mode.INVISIBLE));
    }


    /**
     * Assert that users can not de-restrict themselves
     * @throws MissingCommandParametersException
     */
    @Test
    public void testRestrictMode() throws MissingCommandParametersException{

        MockConnection userA = MockRegisteredConnectionFactory.build();
        userA.getModes().addMode(Mode.RESTRICTED);
        String userANick = userA.getClientInfo().getNick();

        ClientMessage cm = this.commandParser.parse("MODE " + userANick + " -r");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.serverManager);

        assertTrue(userA.getModes().hasMode(Mode.RESTRICTED));
    }

}

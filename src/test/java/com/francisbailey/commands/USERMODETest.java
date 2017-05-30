package com.francisbailey.commands;

import com.francisbailey.MockConnection;
import com.francisbailey.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;
import com.francisbailey.irc.commands.internal.USERMODE;
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
     * Assert that users can not set the operator modes on
     * themselves
     * @throws MissingCommandParametersException
     */
    @Test
    public void testOperatorModeDenied() throws MissingCommandParametersException {

        Connection c = MockRegisteredConnectionFactory.build();
        String nick = c.getClientInfo().getNick();

        ClientMessage cmA = this.cp.parse("MODE " + nick + " +O");
        ClientMessage cmB = this.cp.parse("MODE " + nick + " +o");
        ClientMessage cmC = this.cp.parse("MODE " + nick + " +a");

        Executable exe = new USERMODE();
        exe.execute(c, cmA, this.sm);
        exe.execute(c, cmB, this.sm);
        exe.execute(c, cmC, this.sm);

        assertFalse(c.getModes().hasMode(this.sm, "O"));
        assertFalse(c.getModes().hasMode(this.sm, "o"));
        assertFalse(c.getModes().hasMode(this.sm, "a"));
    }


    /**
     * Assert that users can not set unknown mode types
     * @throws MissingCommandParametersException
     */
    @Test
    public void testNonexistentMode() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        String userANick = userA.getClientInfo().getNick();

        ClientMessage cm = this.cp.parse("MODE " + userANick + " +g");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.sm);

        ServerMessage expected = new ServerMessage(this.sm.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, ": Unknown umode flag");
        assertEquals(expected.compile(), userA.getLastOutput());
        assertFalse(userA.getModes().hasMode(this.sm, "g"));
    }


    /**
     * Assert that user can properly set and unset allowed modes
     * @throws MissingCommandParametersException
     */
    @Test
    public void testModeChange() throws MissingCommandParametersException {
        MockConnection userA = MockRegisteredConnectionFactory.build();
        String userANick = userA.getClientInfo().getNick();

        ClientMessage cmA = this.cp.parse("MODE " + userANick + " +i");
        ClientMessage cmB = this.cp.parse("MODE " + userANick + " -i");
        USERMODE exe = new USERMODE();

        exe.execute(userA, cmA, this.sm);
        assertTrue(userA.getModes().hasMode(this.sm, "i"));

        exe.execute(userA, cmB, this.sm);
        assertFalse(userA.getModes().hasMode(this.sm, "i"));
    }


    /**
     * Assert that users can not set modes for another user
     * @throws MissingCommandParametersException
     */
    @Test
    public void testModeSetOtherUser() throws MissingCommandParametersException {

        MockConnection userA = MockRegisteredConnectionFactory.build();
        MockConnection userB = MockRegisteredConnectionFactory.build();

        String userBNick = userB.getClientInfo().getNick();

        ClientMessage cm = this.cp.parse("MODE " + userBNick + " +v");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.sm);

        ServerMessage expected = new ServerMessage(this.sm.getName(), ServerMessage.ERR_USERSDONTMATCH, ": Can't change mode for other users");

        assertEquals(expected.compile(), userA.getLastOutput());
        assertFalse(userB.getModes().hasMode(this.sm, "v"));
    }


    /**
     * Assert that users can not de-restrict themselves
     * @throws MissingCommandParametersException
     */
    @Test
    public void testRestrictMode() throws MissingCommandParametersException{

        Connection userA = MockRegisteredConnectionFactory.build();
        userA.getModes().addMode(this.sm, "r");
        String userANick = userA.getClientInfo().getNick();

        ClientMessage cm = this.cp.parse("MODE " + userANick + " -r");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.sm);

        assertTrue(userA.getModes().hasMode(this.sm, "r"));
    }

}

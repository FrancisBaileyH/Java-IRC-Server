package com.francisbailey.irc.command;

import com.francisbailey.irc.MockConnection;
import com.francisbailey.irc.MockRegisteredConnectionFactory;
import com.francisbailey.irc.*;
import com.francisbailey.irc.command.internal.USERMODE;
import com.francisbailey.irc.exception.MissingCommandParametersException;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;
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

        ClientMessage cmA = this.cp.parse("MODE " + nick + " +O");
        ClientMessage cmB = this.cp.parse("MODE " + nick + " +o");
        ClientMessage cmC = this.cp.parse("MODE " + nick + " +a");

        Executable exe = new USERMODE();
        exe.execute(c, cmA, this.sm);
        exe.execute(c, cmB, this.sm);
        exe.execute(c, cmC, this.sm);

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
        this.sm.setFindNickValue(userA);

        String userANick = userA.getClientInfo().getNick();

        ClientMessage cm = this.cp.parse("MODE " + userANick + " +g");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.sm);

        ServerMessage expected = new ServerMessage(this.sm.getName(), ServerMessage.ERR_UMODEUNKNOWNFLAG, userANick + " :Unknown umode flag");
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
        this.sm.setFindNickValue(userB);

        ClientMessage cm = this.cp.parse("MODE " + userBNick + " +i");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.sm);

        ServerMessage expected = new ServerMessage(this.sm.getName(), ServerMessage.ERR_USERSDONTMATCH, userA.getClientInfo().getNick() +  " :Can't change mode for other users");

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

        ClientMessage cm = this.cp.parse("MODE " + userANick + " -r");

        Executable exe = new USERMODE();
        exe.execute(userA, cm, this.sm);

        assertTrue(userA.getModes().hasMode(Mode.RESTRICTED));
    }

}

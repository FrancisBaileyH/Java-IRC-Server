package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.*;
import com.francisbailey.irc.exception.MissingCommandParametersException;
import com.francisbailey.irc.mode.Mode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 28/03/18.
 */
public class StandardUserModeStrategyTest extends ModeStrategyTest {


    private UserModeStrategy strategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.strategy = new StandardUserModeStrategy(this.sm);
    }

    /**
     * Assert that users can not set the operator mode or away mode on
     * themselves
     * @throws MissingCommandParametersException
     */
    @Test
    public void testOperatorModeDenied() throws Exception {

        Connection c = MockRegisteredConnectionFactory.build();

        this.strategy.addMode(c, c, Mode.LOCAL_OPERATOR);
        this.strategy.addMode(c, c, Mode.OPERATOR);
        this.strategy.addMode(c, c, Mode.AWAY);

        assertFalse(c.getModes().hasMode(Mode.LOCAL_OPERATOR));
        assertFalse(c.getModes().hasMode(Mode.OPERATOR));
        assertFalse(c.getModes().hasMode(Mode.AWAY));
    }


    /**
     * Assert that modes can be set and unset on a user
     * @throws Exception
     */
    @Test
    public void testSetMode()  throws Exception {
        MockConnection operator = MockRegisteredConnectionFactory.build();
        operator.getModes().addMode(Mode.OPERATOR);

        MockConnection user = MockRegisteredConnectionFactory.build();

        this.strategy.addMode(user, user, Mode.INVISIBLE);
        assertTrue(user.getModes().hasMode(Mode.INVISIBLE));

        this.strategy.removeMode(user, user, Mode.INVISIBLE);
        assertFalse(user.getModes().hasMode(Mode.INVISIBLE));

    }

    /**
     * Assert that users can not de-restrict themselves
     * @throws MissingCommandParametersException
     */
    @Test
    public void testRestrictMode() throws Exception {
        MockConnection user = MockRegisteredConnectionFactory.build();

        user.getModes().addMode(Mode.RESTRICTED);
        this.strategy.removeMode(user, user, Mode.RESTRICTED);
        assertTrue(user.getModes().hasMode(Mode.RESTRICTED));

        MockConnection operator = MockRegisteredConnectionFactory.build();
        operator.getModes().addMode(Mode.OPERATOR);

        this.strategy.removeMode(operator, user, Mode.RESTRICTED);
    }
}
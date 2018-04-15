package com.francisbailey.irc.mode.strategy;

import com.francisbailey.irc.MockServerManager;
import org.junit.Before;

/**
 * Created by fbailey on 28/03/18.
 */
public class ModeStrategyTest {

    protected MockServerManager sm;


    @Before
    public void setUp() throws Exception {
        this.sm = new MockServerManager("mockserver", null);
    }
}

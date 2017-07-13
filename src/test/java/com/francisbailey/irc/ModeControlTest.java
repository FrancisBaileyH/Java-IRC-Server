package com.francisbailey.irc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 12/07/17.
 */
public class ModeControlTest {


    private ModeControl testControl;


    private ModeContext mockContext = new ModeContext() {
        @Override
        public String getContextName() {
            return "test";
        }

        @Override
        public String getContextType() {
            return "test";
        }
    };


    private ModeContext altMockContext = new ModeContext() {
        @Override
        public String getContextName() {
            return "alt";
        }

        @Override
        public String getContextType() {
            return "alt";
        }
    };


    private ModeTarget mockModeTarget = new ModeTarget() {

    };


    @Before
    public void setUp() throws Exception {

        this.testControl = new ModeControl();
        this.testControl.addModeContext(this.mockContext);
    }


    /**
     * Assert that valid modes return as such
     */
    @Test
    public void isValidMode() {

        this.testControl.addModeTypeForContext("f", this.mockContext);
        assertTrue(this.testControl.isValidMode("f", this.mockContext));
        assertFalse(this.testControl.isValidMode("f", this.altMockContext));
        assertFalse(this.testControl.isValidMode("z", this.altMockContext));
    }


    /**
     * Assert that once a mode is added for a context it is valid
     */
    @Test
    public void addModeTypeForContext() {

        assertFalse(this.testControl.isValidMode("b", this.altMockContext));
        this.testControl.addModeTypeForContext("b", this.altMockContext);
        assertTrue(this.testControl.isValidMode("b", this.altMockContext));
    }


    /**
     * Assert hat removing a mode context removes all modes within it
     */
    @Test
    public void removeModeContext() {

        this.testControl.addModeTypeForContext("b", this.altMockContext);
        assertTrue(this.testControl.isValidMode("b", this.altMockContext));
        this.testControl.removeModeContext(this.altMockContext);
        assertFalse(this.testControl.isValidMode("b", this.altMockContext));
    }


    /**
     * Assert that removing a mode type does not remove
     * other mode types unless specified
     */
    @Test
    public void removeModeTypeForContext() {

        this.testControl.addModeTypeForContext("d", this.altMockContext);
        this.testControl.addModeTypeForContext("e", this.altMockContext);
        assertTrue(this.testControl.isValidMode("d", this.altMockContext));
        assertTrue(this.testControl.isValidMode("e", this.altMockContext));

        this.testControl.removeModeTypeForContext("d", this.altMockContext);
        assertFalse(this.testControl.isValidMode("d", this.altMockContext));
        assertTrue(this.testControl.isValidMode("e", this.altMockContext));
    }


    /**
     * Assert that adding modes works as intended and does not
     * add duplicates
     */
    @Test
    public void addTargetMode() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockContext);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockContext);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockContext);

        assertEquals("ab", this.testControl.getTargetModes(this.mockModeTarget, this.mockContext));
        assertTrue(this.testControl.targetHasMode("a", this.mockModeTarget, this.mockContext));
        assertTrue(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockContext));
    }


    /**
     * Assert that removing a target removes all modes
     */
    @Test
    public void removeTarget() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockContext);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockContext);

        this.testControl.removeTarget(this.mockModeTarget);
        assertFalse(this.testControl.targetHasMode("a", this.mockModeTarget, this.mockContext));
        assertFalse(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockContext));
    }


    /**
     * Assert that removing modes for a target does not remove other modes
     * other than the one specified
     */
    @Test
    public void removeTargetMode() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockContext);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockContext);

        this.testControl.removeTargetMode("a", this.mockModeTarget, this.mockContext);
        this.testControl.removeTargetMode("a", this.mockModeTarget, this.mockContext);

        assertFalse(this.testControl.targetHasMode("a", this.mockModeTarget, this.mockContext));
        assertTrue(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockContext));

        this.testControl.removeTargetMode("b", this.mockModeTarget, this.mockContext);
        assertFalse(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockContext));

    }


    /**
     * Assert that modes are properly formatted
     */
    @Test
    public void getTargetModes() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockContext);
        assertEquals("a", this.testControl.getTargetModes(this.mockModeTarget, this.mockContext));

        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockContext);
        this.testControl.addTargetMode("c", this.mockModeTarget, this.mockContext);
        this.testControl.addTargetMode("d", this.mockModeTarget, this.mockContext);

        assertEquals("abcd", this.testControl.getTargetModes(this.mockModeTarget, this.mockContext));
    }

}
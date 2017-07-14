package com.francisbailey.irc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fbailey on 12/07/17.
 */
public class ModeControlTest {


    private ModeControl testControl;


    private ModeResource mockResource = new ModeResource() {
        @Override
        public String getResourceName() {
            return "test";
        }

        @Override
        public String getResourceType() {
            return "test";
        }
    };


    private ModeResource altMockResource = new ModeResource() {
        @Override
        public String getResourceName() {
            return "alt";
        }

        @Override
        public String getResourceType() {
            return "alt";
        }
    };


    private ModeContext mockContext;
    private ModeContext altMockContext;


    private ModeTarget mockModeTarget = () -> "foo-target";


    @Before
    public void setUp() throws Exception {

        this.mockContext = new ModeContext(this.mockModeTarget, this.mockResource);
        this.altMockContext = new ModeContext(this.mockModeTarget, this.altMockResource);
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

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockResource);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockResource);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockResource);

        assertEquals("ab", this.testControl.getTargetModes(this.mockModeTarget, this.mockResource));
        assertTrue(this.testControl.targetHasMode("a", this.mockModeTarget, this.mockResource));
        assertTrue(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockResource));
    }


    /**
     * Assert that removing a target removes all modes
     */
    @Test
    public void removeTarget() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockResource);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockResource);

        this.testControl.removeTarget(this.mockModeTarget);
        assertFalse(this.testControl.targetHasMode("a", this.mockModeTarget, this.mockResource));
        assertFalse(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockResource));
    }


    /**
     * Assert that removing modes for a target does not remove other modes
     * other than the one specified
     */
    @Test
    public void removeTargetMode() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockResource);
        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockResource);

        this.testControl.removeTargetMode("a", this.mockModeTarget, this.mockResource);
        this.testControl.removeTargetMode("a", this.mockModeTarget, this.mockResource);

        assertFalse(this.testControl.targetHasMode("a", this.mockModeTarget, this.mockResource));
        assertTrue(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockResource));

        this.testControl.removeTargetMode("b", this.mockModeTarget, this.mockResource);
        assertFalse(this.testControl.targetHasMode("b", this.mockModeTarget, this.mockResource));

    }


    /**
     * Assert that modes are properly formatted
     */
    @Test
    public void getTargetModes() {

        this.testControl.addTargetMode("a", this.mockModeTarget, this.mockResource);
        assertEquals("a", this.testControl.getTargetModes(this.mockModeTarget, this.mockResource));

        this.testControl.addTargetMode("b", this.mockModeTarget, this.mockResource);
        this.testControl.addTargetMode("c", this.mockModeTarget, this.mockResource);
        this.testControl.addTargetMode("d", this.mockModeTarget, this.mockResource);

        assertEquals("abcd", this.testControl.getTargetModes(this.mockModeTarget, this.mockResource));
    }

}
package com.francisbailey.irc.command;


import com.francisbailey.irc.*;
import com.francisbailey.irc.exception.MissingCommandParametersException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class OPERTest extends CommandTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.serverManager.getConfig().getOperators().clear();
    }


    @Test
    public void testInvalidPassword() throws MissingCommandParametersException {

        MockConnection connection = MockRegisteredConnectionFactory.build();
        Executable exe = new OPER();
        ClientMessage clientMessage = this.commandParser.parse("OPER badUser badPassword");

        exe.execute(connection, clientMessage, this.serverManager);

        ServerMessage serverMessage = connection.getLastOutput();

        assertEquals(serverMessage.getServerReply(), ServerMessage.ERR_PASSWDMISMATCH);
    }


    @Test
    public void testValidPassword() throws Exception {

        String username = "foobar";
        String password = "easypass";

        this.serverManager.getConfig().getOperators().put(username, password);

        MockConnection connection = MockRegisteredConnectionFactory.build();
        Executable exe = new OPER();
        ClientMessage clientMessage = this.commandParser.parse("OPER " + username + " " + password);

        exe.execute(connection, clientMessage, this.serverManager);

        ArrayList<ServerMessage> messages = connection.getAllOutput();
        ServerMessage serverMessage = messages.get(0);

        assertEquals(serverMessage.getServerReply(), ServerMessage.RPL_YOUREOP);
    }

}

package com.francisbailey.irc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by fbailey on 02/11/16.
 */
public class Server implements ConnectionDelegate, ServerManager {


    private ServerSocket socket;
    private ArrayList<Connection> connections;
    private ArrayList<Connection> registeredConnections;
    private Config config;
    private CommandParser parser;
    private CommandFactory cf;
    private ChannelManager cm;
    private String name;
    private UserModes userModes;


    /**
     * Initialize server socket on specified port.
     * @param port
     */
    public Server(CommandParser parser, CommandFactory cf, Config config, int port) {

        this.connections = new ArrayList<>();
        this.registeredConnections = new ArrayList<>();
        this.userModes = new UserModes();
        this.parser = parser;
        this.cf = cf;
        this.config = config;
        this.cm = new ChannelManager(config.channels);
        this.name = config.serverName;

        try {
            this.socket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Begin listening for and accepting incoming connections. Initialize
     * new connection objects and run them. Pass in the server as a connection
     * delegate.
     */
    public void listen() {

        try {
            while (true) {

                Socket s = this.socket.accept();
                TCPConnection c = new TCPConnection(s, this);

                this.connections.add(c);
                Thread t = new Thread(c);
                t.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parse and execute incoming String commands from connections.
     * @param c
     * @param command
     */
    public void receive(Connection c, String command) {

        try {
            ClientMessage cm = this.parser.parse(command);
            System.out.println(cm.getMessage());
            Executable e = this.cf.build(cm);

            if (cm.getParameterCount() < e.getMinimumParams()) {
                c.send(new ServerMessage(this.name, ServerMessage.ERR_NEEDMOREPARAMS, cm.getCommand() + " :Not enough parameters"));
            }
            else if (!c.isRegistered() && !e.canExecuteUnregistered()) {
                c.send(new ServerMessage(this.name, ServerMessage.ERR_NOTREGISTERED, ""));
            }
            else {
                e.execute(c, cm, this);
            }
        }
        catch (MissingCommandParametersException e) {
            c.send(new ServerMessage(this.name, ServerMessage.ERR_NEEDMOREPARAMS, ":Not enough parameters"));
        }
        catch (InvalidCommandException e) {
            String message = "";
            // send error to client
            if (c.isRegistered()) {
                message = c.getClientInfo().getHostmask();
            }
            c.send(new ServerMessage(this.name, ServerMessage.ERR_UNKNOWNCOMMAND, message));
        }
    }


    /**
     * Clean up any connection references and then terminate the connection
     * @param c
     */
    public synchronized void closeConnection(Connection c) {

        this.connections.remove(c);
        this.registeredConnections.remove(c);
        c.terminate();
    }


    /**
     * Add the user to the registered connections list and
     * set any registration info to the connection.
     * @param c
     * @param clientInfo
     */
    public synchronized void registerConnection(Connection c, Client clientInfo) {

        c.register(clientInfo);
        this.registeredConnections.add(c);
        this.connections.remove(c);
    }


    /**
     * See if a registered connection exists with a given nick
     * @param nick
     * @return
     */
    public synchronized Connection findConnectionByNick(String nick) {

        for (Connection c: this.registeredConnections) {

            if (c.getClientInfo().getNick().equals(nick)) {
                return c;
            }
        }

        return null;
    }


    /**
     * Get the configuration object
     * @return
     */
    public Config getConfig() {
        return this.config;
    }


    /**
     * Get the channel manager.
     * @return
     */
    public ChannelManager getChannelManager() {
        return this.cm;
    }


    /**
     * Retrieve the server name as defined in the config.
     * @return
     */
    public String getName() {
        return this.name;
    }


    /**
     * Send a message to all registered connections.
     * @param sm
     */
    public void broadcast(ServerMessage sm) {

        for (Connection c: this.registeredConnections) {
            c.send(sm);
        }
    }

    @Override
    public UserModes getModeTypes() {
        return this.userModes;
    }


    @Override
    public String getContextName() {
        return config.networkName;
    }
}

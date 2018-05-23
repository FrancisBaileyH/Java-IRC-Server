package com.francisbailey.irc;

import com.francisbailey.irc.exception.InvalidCommandException;
import com.francisbailey.irc.exception.MissingCommandParametersException;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.strategy.ChannelModeArgStrategy;
import com.francisbailey.irc.mode.strategy.ChannelModeMaskStrategy;
import com.francisbailey.irc.mode.strategy.ChannelModeStrategy;
import com.francisbailey.irc.mode.strategy.ChannelUserModeStrategy;
import com.francisbailey.irc.mode.strategy.StandardChannelModeStrategy;
import com.francisbailey.irc.mode.strategy.StandardUserModeStrategy;
import com.francisbailey.irc.mode.strategy.UserModeStrategy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by fbailey on 02/11/16.
 */
public class Server implements ConnectionDelegate, ServerManager, Loggable {


    private ServerSocket socket;
    private ArrayList<Connection> connections;
    private ArrayList<Connection> registeredConnections;
    private Config config;
    private CommandParser parser;
    private CommandFactory cf;
    private ChannelManager cm;
    private String name;
    private HashMap<Mode, ChannelModeStrategy> channelModes;
    private HashMap<Mode, UserModeStrategy> userModes;


    /**
     * Initialize server socket on specified port.
     * @param port
     */
    public Server(CommandParser parser, CommandFactory cf, Config config, int port) {

        this.connections = new ArrayList<>();
        this.registeredConnections = new ArrayList<>();
        this.parser = parser;
        this.cf = cf;
        this.config = config;
        this.cm = new ChannelManager(config.getChannels());
        this.name = config.getServerName();

        this.channelModes = new HashMap<>();
        this.userModes = new HashMap<>();

        ChannelModeArgStrategy channelModeArgStrategy = new ChannelModeArgStrategy(this);
        StandardChannelModeStrategy standardChannelModeStrategy = new StandardChannelModeStrategy(this);
        ChannelUserModeStrategy channelUserModeStrategy = new ChannelUserModeStrategy(this);
        ChannelModeMaskStrategy channelModeMaskStrategy = new ChannelModeMaskStrategy(this);
        StandardUserModeStrategy standardUserModeStrategy = new StandardUserModeStrategy(this);


        this.channelModes.put(Mode.VOICE,              channelUserModeStrategy);
        this.channelModes.put(Mode.OWNER,              channelUserModeStrategy);
        this.channelModes.put(Mode.CHAN_OPERATOR,      channelUserModeStrategy);
        this.channelModes.put(Mode.INVITE,             standardChannelModeStrategy);
        this.channelModes.put(Mode.MODERATED,          standardChannelModeStrategy);
        this.channelModes.put(Mode.CHAN_SRC_ONLY,      standardChannelModeStrategy);
        this.channelModes.put(Mode.QUIET,              standardChannelModeStrategy);
        this.channelModes.put(Mode.PRIVATE,            standardChannelModeStrategy);
        this.channelModes.put(Mode.SECRET,             standardChannelModeStrategy);
        this.channelModes.put(Mode.REOP,               standardChannelModeStrategy);
        this.channelModes.put(Mode.OP_TOPIC_ONLY,      standardChannelModeStrategy);
        this.channelModes.put(Mode.CHAN_KEY,           channelModeArgStrategy);
        this.channelModes.put(Mode.USER_LIMIT,         channelModeArgStrategy);
        this.channelModes.put(Mode.BAN_MASK,           channelModeMaskStrategy);
        this.channelModes.put(Mode.BAN_MASK_EXCEPTION, channelModeMaskStrategy);
        this.channelModes.put(Mode.INVITATION_MASK,    channelModeMaskStrategy);

        this.userModes.put(Mode.OPERATOR,              standardUserModeStrategy);
        this.userModes.put(Mode.LOCAL_OPERATOR,        standardUserModeStrategy);
        this.userModes.put(Mode.AWAY,                  standardUserModeStrategy);
        this.userModes.put(Mode.WALLOPS,               standardUserModeStrategy);
        this.userModes.put(Mode.RESTRICTED,            standardUserModeStrategy);
        this.userModes.put(Mode.SNOTICE,               standardUserModeStrategy);
        this.userModes.put(Mode.VOICE,                 standardUserModeStrategy);
        this.userModes.put(Mode.INVISIBLE,             standardUserModeStrategy);

        try {
            logger().info("Listening on: {}", port);
            this.socket = new ServerSocket(port);
        }
        catch (IOException e) {
            logger().error("Exiting. Failed to initiate socket. {}", e.getStackTrace());
        }
    }


    /**
     * Bad implementation... let's just do this and move on...
     * @param mode
     * @return
     */
    public ChannelModeStrategy getChannelModeStrategy(Mode mode) {
        return this.channelModes.get(mode);
    }


    public UserModeStrategy getUserModeStrategy(Mode mode) {
        return this.userModes.get(mode);
    }


    public boolean isChannelMode(String flag) {
        return this.channelModes.containsKey(flag);
    }


    public boolean isUserMode(String flag) {
        return this.userModes.containsKey(flag);
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
                String uuid = UUID.randomUUID().toString();
                IrcConnection c = new IrcConnection(s, this, uuid);

                this.connections.add(c);
                Thread t = new Thread(c);
                t.start();
            }
        }
        catch (IOException e) {
            logger().error("Unable to to accept connection");
            logger().debug(e.getStackTrace());
        }
    }


    /**
     * Parse and execute incoming String command from connections.
     * @param c
     * @param command
     */
    public void receive(Connection c, String command) {

        try {
            ClientMessage cm = this.parser.parse(command);
            logger().debug(cm.getMessage());
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

        try {
            this.connections.remove(c);
            this.registeredConnections.remove(c);
            c.terminate();
        } catch (Exception e) {
            logger().error("An error occurred while closing connection");
            logger().debug(e.getStackTrace());
        }
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
}

package com.francisbailey.irc;

import com.francisbailey.irc.message.SendableMessage;
import com.francisbailey.irc.mode.ModeSet;

import java.io.*;
import java.net.Socket;

/**
 * Created by fbailey on 02/11/16.
 */

/**
 * @TODO PING Timer & Registration Timer
 */
public class IrcConnection implements Runnable, Connection, Loggable {


    private Socket socket;
    private Boolean terminated;
    private BufferedReader in;
    private BufferedWriter out;
    private Boolean registered;
    private Client clientInfo;
    private ConnectionDelegate delegate;
    private ModeSet modes;
    private String id;


    public IrcConnection(Socket s, ConnectionDelegate d, final String id) {
        this.socket = s;
        this.delegate = d;
        this.terminated = false;
        this.registered = false;
        this.clientInfo = new Client();
        this.modes = new ModeSet();
        this.id = id;
    }


    /**
     * @TODO - this is susceptible to a readLine DOS attack. We need a bounded buffered reader
     * @TODO - add IOException handling (close out connection)
     * Initialize input and output streams and begin listening for
     * incoming messages.
     */
    public void run() {

        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            while (!this.terminated) {
                this.process(this.in.readLine());
            }

            this.socket.close();
        }
        catch (IOException e) {
            logger().error("Connection terminated unexpectedly");
            logger().error("Cleaning up connection");
            logger().debug(e.getStackTrace());

            this.delegate.closeConnection(this);
        }
    }


    /**
     *
     * Pass the job of command parsing and execution back to the Server object.
     * @param command
     */
    private void process(String command) {

        if (command != null) {
            this.delegate.receive(this, command);
        }
        else {
            this.delegate.closeConnection(this);
        }
    }


    /**
     * Terminate the connection.
     */
    public void terminate() {
        this.terminated = true;
    }


    /**
     * Send a generic message back to the client.
     * @param message
     */
    public void send(SendableMessage message) {

        String output = message.compile();
        logger().debug("Sending: {}", output);

        try {
            this.out.write(output);
            this.out.flush();
        }
        catch (IOException e) {
            logger().error("Unable to write to connection.");
            logger().debug(e.getStackTrace());
        }
    }


    /**
     * The client is now registered and can accept and execute more command
     * on the server. ClientInfo is also attached to the connection.
     * @param clientInfo
     */
    public void register(Client clientInfo) {
        this.clientInfo = clientInfo;
        this.registered = true;
    }


    public Boolean isRegistered() {
        return this.registered;
    }

    public Client getClientInfo() {
        return this.clientInfo;
    }

    public void setClientInfo(Client c) {
        this.clientInfo = c;
    }

    public String getHostNameInfo() {

        return this.socket.getInetAddress().getCanonicalHostName();
    }

    public ModeSet getModes() {
        return this.modes;
    }

    public void setModes(ModeSet s) {
        this.modes = s;
    }

    public final String getId() {
        return this.id;
    }
}

package com.francisbailey.irc;

import java.io.*;
import java.net.Socket;

/**
 * Created by fbailey on 02/11/16.
 */

/**
 * @TODO PING Timer & Registration Timer
 */
public class TCPConnection implements Runnable, Connection {


    private Socket socket;
    private Boolean terminated;
    private BufferedReader in;
    private BufferedWriter out;
    private Boolean registered;
    private Client clientInfo;
    private ConnectionDelegate delegate;


    public TCPConnection(Socket s, ConnectionDelegate d) {
        this.socket = s;
        this.delegate = d;
        this.terminated = false;
        this.registered = false;
        this.clientInfo = new Client(null, null, null, null);
    }


    /**
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
        catch (Exception e) {
            e.printStackTrace();
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
        System.out.println(output);
        try {
            this.out.write(output);
            this.out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * The client is now registered and can accept and execute more commands
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

}

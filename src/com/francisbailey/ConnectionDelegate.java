package com.francisbailey;

/**
 * Created by fbailey on 04/11/16.
 */
public interface ConnectionDelegate {

    public void receive(Connection c, String command);
    public void closeConnection(Connection c);
}

package com.francisbailey.irc;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public interface Loggable {

    default Logger logger() {
        return LogManager.getLogger(getClass());
    }
}

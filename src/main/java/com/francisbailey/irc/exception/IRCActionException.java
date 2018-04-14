package com.francisbailey.irc.exception;

/**
 * Created by fbailey on 12/04/18.
 */
public class IRCActionException extends Exception {

    private String replyCode;
    private String message;

    public IRCActionException(String replyCode, String message) {
        this.replyCode = replyCode;
        this.message = message;
    }


    public String getReplyCode() {
        return this.replyCode;
    }


    public String getMessage() {
        return this.message;
    }
}

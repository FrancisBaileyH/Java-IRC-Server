package com.francisbailey.irc.message;

public interface ServerMessageBuilderWithMessage extends ServerMessageBuilderResult {
    public ServerMessageBuilderResult andMessage(String message);
}

package com.francisbailey.irc.message;

public interface ServerMessageBuilderWithReplyCode {
    public ServerMessageBuilderWithMessage withReplyCode(String replyCode);
}

package com.francisbailey.irc.message;

public class ServerMessageBuilder implements ServerMessageBuilderWithReplyCode, ServerMessageBuilderWithMessage, ServerMessageBuilderResult {


    private String serverReplyCode = "";
    private String message = "";
    public String origin;


    public static ServerMessageBuilderWithReplyCode from(String origin) {
        return new ServerMessageBuilder(origin);
    }


    private ServerMessageBuilder(String origin) {
        this.origin = origin;
    }


    public ServerMessageBuilderWithMessage withReplyCode(String serverRelyCode) {
        this.serverReplyCode = serverRelyCode;

        return this;
    }


    public ServerMessageBuilderResult andMessage(String message) {
        this.message = message;

        return this;
    }

    public ServerMessage build() {

        return new ServerMessage(this.origin, this.serverReplyCode, this.message);
    }

}

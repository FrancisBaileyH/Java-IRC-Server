package com.francisbailey;

/**
 * Created by fbailey on 04/11/16.
 */
public class Client {

    private String nick = "*";
    private String username;
    private String hostName;
    private String realName;


    public Client(String nick, String username, String hostName, String realName) {

        this.nick = nick;
        this.username = username;
        this.hostName = hostName;
        this.realName = realName;
    }


    /**
     *  Compile the nick, username and hostname into an IRC hostmask.
     *
     * @return String
     */
    public String getHostmask() {
        return this.nick + "!" + this.username + "@" + this.hostName;
    }


    /**
     * Getters and setters for client properties.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setHostname(String name) {
        this.hostName = name;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void setRealname(String name) {
        this.realName = name;
    }

    public String getNick() {
        return this.nick;
    }

    public String getHostname() {
        return this.hostName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRealname() {
        return this.realName;
    }

}

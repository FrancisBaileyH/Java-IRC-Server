package com.francisbailey;

import java.util.ArrayList;

/**
 * Created by fbailey on 16/11/16.
 */
public class Config {

    public ArrayList<String> commands;
    public final int MAX_NICK_LEN = 9;

    public String serverName = "cosc318server";
    public String welcomeMessage = "Welcome to " + serverName + "!";
    public String motd = "\n\n/===================\\\n|  JAVA IRC Server  |\n\\===================/\n\n";
    public String[] channels = {"#general", "#java", "#cosc318", "#networking"};


    public Config() {

    }

}

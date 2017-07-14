package com.francisbailey.irc;


import java.util.HashMap;

/**
 * Created by fbailey on 13/07/17.
 */
public class MockConfig {


    public HashMap<String, String[]> defaultModes;


    public MockConfig() {

        this.defaultModes = new HashMap<>();

        this.defaultModes.put("server-mock-user", new String[]{
                "i", "a", "w", "r", "o", "O", "s"
        });

        this.defaultModes.put("server-mock-channel", new String[]{
                "a", "i", "m", "n", "q", "p", "s", "r", "t", "k", "l", "b", "e"
        });

        this.defaultModes.put("channel-mock-user", new String[]{
                "o", "O", "v"
        });
    }

}

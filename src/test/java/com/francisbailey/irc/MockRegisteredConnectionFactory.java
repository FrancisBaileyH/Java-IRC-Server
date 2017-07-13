package com.francisbailey.irc;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fbailey on 05/05/17.
 */
public class MockRegisteredConnectionFactory {

    public static MockConnection build() {
        String nick = createRandomCode(6);
        String username = createRandomCode(7);
        String hostname = createRandomCode(12);
        String realName = createRandomCode(6);

        Client cli = new Client(nick, username, hostname, realName);

        MockConnection c = new MockConnection();
        c.register(cli);

        return c;
    }


    public static String createRandomCode(int codeLength) {

        String id = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        List<Character> temp = id.chars()
                .mapToObj(i -> (char)i)
                .collect(Collectors.toList());
        Collections.shuffle(temp, new SecureRandom());
        return temp.stream()
                .map(Object::toString)
                .limit(codeLength)
                .collect(Collectors.joining());
    }


}

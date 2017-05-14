# Java IRC Server
Work in progress IRC server

## Building

```
cc /path/to/src
javac -sourcepath . com/francisbailey/irc/Main.java
```

## Running

```
cd /path/to/src
java com.francisbailey.irc.Main
```

Now you can simply connect to localhost on port 6667 with your irc client.


## Milestones

#### Milestone 1 - Channel/Private Messaging
- [x] Implement user registration via USER & NICK commands
- [x] Implement channel messaging via JOIN, PART, WHO and PRIVMSG commands
- [x] Basic MODE command implementation
- [x] Implement channel management
- [x] Implement dynamic configuration via XML config file
- [x] Implement OPER, MOTD, PING, QUIT commands

#### Milestone 2 - Chan Ops:
- [ ] Implement Channel Modes & Update User Modes
- [ ] NAMES command
- [ ] KICK command
- [ ] TOPIC command
- [ ] INVITE command

#### Milestone 3 - Server Protection:
- [ ] Flood Protection and Penalties
- [ ] Server Bans

#### Milestone 4 - Server Queries
- [ ] VERSION command
- [ ] STATS command
- [ ] TIME command
- [ ] ADMIN command
- [ ] INFO command
- [ ] LUSERS command

#### Milestone 5
- [ ] Configuration File Validation
- [ ] SSL Connections
- [ ] Server Links
# Java IRC Server
Work in progress IRC server

## Building

```
cc /path/to/src
javac -sourcepath . com/francisbailey/Main.java
```

## Running

```
cd /path/to/src
java com.francisbailey.Main
```

Now you can simply connect to localhost on port 6667 with your irc client.


## Milestones
#### Milestone 1 - Chan Ops:
- [ ] Implement Channel Modes & Update User Modes
- [ ] NAMES command
- [ ] KICK command
- [ ] TOPIC command
- [ ] INVITE command

#### Milestone 2 - Server Protection:
- [ ] Flood Protection and Penalties
- [ ] Server Bans

#### Milestone 3 - Server Queries
- [ ] VERSION command
- [ ] STATS command
- [ ] TIME command
- [ ] ADMIN command
- [ ] INFO command
- [ ] LUSERS command

#### Milestone 4
- [ ] Configuration File Validation
- [ ] SSL Connections
- [ ] Server Links
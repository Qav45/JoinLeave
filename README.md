# JoinLeave

A Paper 1.21 plugin that gives server owners and players full control over join and leave messages.

## Features

- Custom server-wide join and leave messages with `&` color codes and hex colors (`&#RRGGBB`)
- First-join message shown only on a player's very first connection
- Per-player custom messages — players with permission can set their own join/leave text
- PlaceholderAPI support (soft-depend — works without it)
- Welcome title screen on join
- Hide join/leave messages for staff via `hide.players` permission
- Toggle all messages or only leave messages from config
- `/joinleave reload` to apply config changes live

## Commands

| Command | Description | Permission |
|---|---|---|
| `/joinleave reload` | Reload config and data from disk | `joinleave.reload` (op) |
| `/joinleave setjoin <message>` | Set your personal join message | `joinleave.custom` |
| `/joinleave setleave <message>` | Set your personal leave message | `joinleave.custom` |
| `/joinleave resetjoin` | Clear your custom join message | `joinleave.custom` |
| `/joinleave resetleave` | Clear your custom leave message | `joinleave.custom` |

Alias: `/join-leave`

## Permissions

| Node | Default | Description |
|---|---|---|
| `joinleave.reload` | op | Reload the plugin config |
| `joinleave.custom` | false | Set/reset personal join and leave messages |
| `hide.players` | false | Suppresses this player's join/leave messages when `Hide-Players-With-Perms: true` |

## Installation

1. Drop `JoinLeave-1.1.0.jar` into your server's `plugins/` folder.
2. Restart the server.
3. Edit `plugins/JoinLeave/config.yml` to your liking.
4. Run `/joinleave reload` to apply changes without a restart.

## Configuration

```yaml
# plugins/JoinLeave/config.yml

Join: '&7[&a+&7] &a%player%'
Leave: '&7[&a-&7] &7%player%'

First-Join-Message: true
First-Join: '&7[&a+&7] &a%player% &7welcome to the server! use &a/welcome'

Title-Onjoin: true

Hide-Players-With-Perms: false
Disable-Join-LeaveMessage: false
Disable-Leave-Message: false
```

Use `%player%` for the player's name. PlaceholderAPI placeholders like `%player_displayname%` also work if PAPI is installed.

Color codes: `&a` green, `&7` gray, `&#FF5500` hex orange, etc.

## Custom Player Messages

Grant a player `joinleave.custom` (e.g. via LuckPerms):

```
/lp user Steve permission set joinleave.custom true
```

The player can then run:

```
/joinleave setjoin &6Steve &ehas arrived!
/joinleave setleave &6Steve &ehas left the building.
/joinleave resetjoin
```

Custom messages support the same color codes and PlaceholderAPI placeholders as the global config. Messages are saved to `plugins/JoinLeave/data.yml` and persist across restarts.

## Requirements

- Paper 1.21.1+
- Java 21+
- PlaceholderAPI (optional)

## Building from source

```bash
git clone https://github.com/Qav45/JoinLeave.git
cd JoinLeave
mvn clean package
# JAR is in target/JoinLeave-1.1.0.jar
```

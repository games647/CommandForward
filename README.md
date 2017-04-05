# CommandForward

## Description

This lightweight plugin to forward commands from the bukkit-side to a bungeecord server. It uses plugin-channels to
communicate between the server instances. This part of a server->proxy->player connection and requires at least one
player to be online. Therefore we don't need to open a port and establish a new connection between the servers.
Furthermore on a RedisBungee environment it forwards the command only to currently used proxy by that specified player.

I'm open for suggestions.

If you like the project, leave a star on Github and contribute there.

## Features
* Lightweight
* Easy to use
* RedisBungee support (executes command only on the proxy of that player)
* Forwarding as Player or Console

## Warning
* This plugin cannot forward commands to a bungeecord server if no player is online

## How to use

### Setup

* Drop the plugin in the bungee and bukkit server
* Finished setup

### Using it

#### Execute this command on the bukkit side
`/forward bridgePlayer cmd [args...]`

bridgePlayer is the player which connection should be used.
This is relevant for receiving the output or selecting the correct proxy in a redisbungee environment

cmd is the start of the actual command
[] means it's optional

##### Example Execute as Player:
`/forward playerName ping`

##### Example Execute as Console:
`/forward console ping`

This will select a random player to forward the connection to the bungee server, but it will be executed as bungee
console there.


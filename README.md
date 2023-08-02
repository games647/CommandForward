# CommandForward

## Description

This lightweight plugin to forward commands from the Bukkit-side to a Bungeecord server. It uses plugin-channels to
communicate between the server instances. This part of a server->proxy->player connection and requires at least one
player to be online. Therefore, we don't need to open a port and establish a new connection between the servers.
Furthermore, on a RedisBungee environment it forwards the command only to currently used proxy by that specified player.

I'm open for suggestions.

If you like the project, leave a star on GitHub and contribute there.

## Features
* Lightweight
* Easy to use
* RedisBungee support (executes command only on the proxy of that player)
* Forwarding as Player or Console

## Warning
* This plugin cannot forward commands to a Bungeecord server if no player is online

## How to use

### Setup

* Drop the plugin in the Bungee and Bukkit server
* Finished setup

## Permissions

commandforward.bukkit.command.forward.* - Allow to use all features of forward command
commandforward.bukkit.command.forward - Allow to use base forward command
commandforward.bukkit.command.forward.console - Allow to use console in forward command
commandforward.bukkit.command.forward.other - Allow to use another player than themselves in forward command

### Using it

#### Execute this command on the Bukkit side
`/forward bridgePlayer cmd [args...]`

bridgePlayer is the player which connection should be used.
This is relevant for receiving the output or selecting the correct proxy in a RedisBungee environment.

cmd is the start of the actual command `[]` means it's optional

##### Example Execute as Self:
`/intercept ping`

This will select the command invoker for forwarding the data or random player if the invoker
is the console.

##### Example Execute as Player:
`/forward playerName ping`
add：
`/forward @s ping`

##### Example Execute as Console:
`/forward console ping`

This will select a random player to forward the connection to the Bungee server, but it will be executed as Bungee
console there.

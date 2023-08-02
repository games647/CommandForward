package com.github.games647.commandforward.bukkit.command;

import com.github.games647.commandforward.bukkit.CommandForwardBukkit;
import com.github.games647.commandforward.bukkit.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForwardCommand extends MessageCommand {

    // it starts with the player name, followed by the command and then the arg starts
    private final int ARG_START = 2;

    public ForwardCommand(CommandForwardBukkit plugin, String channel, String messagePrefix) {
        super(plugin, channel, messagePrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            sendErrorMessage(sender, "Wrong command, Missing arguments");
            return false;
        }

        String channelPlayer = args[0];

        // Check if the first argument is "@s" and replace it with the name of the command sender
        if ("@s".equalsIgnoreCase(channelPlayer) && sender instanceof Player) {
            channelPlayer = ((Player) sender).getName();
        }

        if ("Console".equalsIgnoreCase(channelPlayer)) {
            if (!Permissions.FORWARD_CONSOLE.isSetOn(sender)) {
                sendErrorMessage(sender, Permissions.ERROR_MESSAGE);
                return true;
            }

            Bukkit.getOnlinePlayers().stream().findAny().ifPresent(messageSender -> {
                sendForwardCommand(messageSender, false, args[1], dropFirstArgs(args, ARG_START), sender.isOp());
            });
        } else {
            if (sender instanceof Player && !sender.getName().equalsIgnoreCase(channelPlayer)
                    && !Permissions.FORWARD_OTHER.isSetOn(sender)) {
                sendErrorMessage(sender, Permissions.ERROR_MESSAGE);
                return true;
            }

            if (Bukkit.getServer().getPlayer(channelPlayer) == null) {
                sendErrorMessage(sender, "Player '" + channelPlayer + "' not found");
                return true;
            }

            Player messageSender = Bukkit.getServer().getPlayer(channelPlayer);
            if (messageSender != null) {
                sendForwardCommand(messageSender, true, args[1], dropFirstArgs(args, ARG_START), sender.isOp());
            }
        }

        return true;
    }
}

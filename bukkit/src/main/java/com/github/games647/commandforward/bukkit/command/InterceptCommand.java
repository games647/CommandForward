package com.github.games647.commandforward.bukkit.command;

import com.github.games647.commandforward.bukkit.CommandForwardBukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InterceptCommand extends MessageCommand {

    // arguments starts after the command
    private static final int ARG_START = 1;

    public InterceptCommand(CommandForwardBukkit plugin, String messageChannel, String prefix) {
        super(plugin, messageChannel, prefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            sendErrorMessage(sender, "Missing command to invoke");
            return false;
        }

        if (sender instanceof Player) {
            Player messageSender = (Player) sender;
            sendForwardCommand(messageSender, true, args[0], dropFirstArgs(args, ARG_START), sender.isOp());
            return true;
        }

        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(messageSender -> {
            sendForwardCommand(messageSender, false, args[0], dropFirstArgs(args, ARG_START), sender.isOp());
        });

        return true;
    }
}

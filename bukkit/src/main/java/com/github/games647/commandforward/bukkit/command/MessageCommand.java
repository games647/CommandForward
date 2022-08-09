package com.github.games647.commandforward.bukkit.command;

import com.github.games647.commandforward.bukkit.CommandForwardBukkit;
import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

public abstract class MessageCommand implements CommandExecutor {

    protected final CommandForwardBukkit plugin;
    protected final String channel;
    protected final String messagePrefix;

    public MessageCommand(CommandForwardBukkit plugin, String channel, String messagePrefix) {
        this.plugin = plugin;
        this.channel = channel;
        this.messagePrefix = messagePrefix;
    }

    /**
     * Print an error message
     *
     * @param sender  Sender that execute the current command
     * @param message Message to send to command sender
     */
    protected void sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(messagePrefix + ChatColor.RED + message);
    }

    protected void sendForwardCommand(PluginMessageRecipient sender, boolean isPlayer, String command,
                                      String[] args, boolean isOp) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeBoolean(isPlayer);

        dataOutput.writeUTF(command);
        dataOutput.writeUTF(Joiner.on(' ').join(args));
        dataOutput.writeBoolean(isOp);
        sender.sendPluginMessage(plugin, channel, dataOutput.toByteArray());
    }

    protected String[] dropFirstArgs(String[] args, int pos) {
        return Arrays.copyOfRange(args, pos, args.length);
    }
}

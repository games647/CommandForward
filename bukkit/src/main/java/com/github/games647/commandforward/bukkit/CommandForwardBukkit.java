package com.github.games647.commandforward.bukkit;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandForwardBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args.length == 1) {
            sender.sendMessage(ChatColor.DARK_RED + "Command is missing");
        } else {
            String channelPlayer = args[0];

            Player messageSender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

            ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
            if (channelPlayer.equalsIgnoreCase("Console")) {
                dataOutput.writeBoolean(true);
            } else {
                dataOutput.writeBoolean(!(sender instanceof Player));
                messageSender = getServer().getPlayer(channelPlayer);
            }

            if (messageSender == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Player not online for fowarding this command");
            } else {
                dataOutput.writeUTF(args[1]);
                dataOutput.writeUTF(Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length)));
                messageSender.sendPluginMessage(this, getName(), dataOutput.toByteArray());
            }
        }

        return true;
    }
}

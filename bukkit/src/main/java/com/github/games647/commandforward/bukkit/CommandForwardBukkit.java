package com.github.games647.commandforward.bukkit;

import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandForwardBukkit extends JavaPlugin {

    private static final String MESSAGE_CHANNEL = "commandforward:cmd";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, MESSAGE_CHANNEL);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args.length == 1) {
            sender.sendMessage(ChatColor.RED + "[CommandForward] Command is missing");
        } else {
            String channelPlayer = args[0];

            Optional<? extends Player> optPlayer = Bukkit.getOnlinePlayers().stream().findAny();
            if (!optPlayer.isPresent()) {
                sender.sendMessage(ChatColor.RED + "[CommandForward] Player not online for forwarding this command");
                return false;
            }

            Player messageSender = optPlayer.get();

            ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
            if ("Console".equalsIgnoreCase(channelPlayer)) {
                dataOutput.writeBoolean(false);
            } else {
                // How do I make messageSender equal Player or null on check? Probably not possible in Java?
                if(getServer().getPlayer(channelPlayer) == null) {
                  sender.sendMessage(ChatColor.RED + "[CommandForward] Player, " + channelPlayer + ", not found");
                  return true;
                }

                dataOutput.writeBoolean(sender instanceof Player);
                messageSender = getServer().getPlayer(channelPlayer);
            }

            dataOutput.writeUTF(args[1]);
            dataOutput.writeUTF(Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length)));
            dataOutput.writeBoolean(sender.isOp());
            messageSender.sendPluginMessage(this, MESSAGE_CHANNEL, dataOutput.toByteArray());
        }

        return true;
    }
}

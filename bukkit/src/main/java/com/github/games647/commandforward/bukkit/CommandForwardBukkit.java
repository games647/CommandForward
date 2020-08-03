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
    private final String PLUGIN_NAME_PREFIX = ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + this.getName() + ChatColor.DARK_AQUA + "] ";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, MESSAGE_CHANNEL);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            sendErrorMessage(sender, "Wrong command, Missing arguments");
            return false;
        }

        String channelPlayer = args[0];

        Optional<? extends Player> optPlayer = Bukkit.getOnlinePlayers().stream().findAny();
        if (!optPlayer.isPresent()) {
            sendErrorMessage(sender, "No player is online to forward this command");
            return true;
        }

        Player messageSender = optPlayer.get();

        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        if ("Console".equalsIgnoreCase(channelPlayer)) {
            if (!Permissions.FORWARD_CONSOLE.isSetOn(sender)) {
                sendErrorMessage(sender, Permissions.ERROR_MESSAGE);
                return true;
            }

            dataOutput.writeBoolean(false);
        } else {
            if(sender instanceof Player && !sender.getName().equalsIgnoreCase(channelPlayer) && !Permissions.FORWARD_OTHER.isSetOn(sender)) {
                sendErrorMessage(sender, Permissions.ERROR_MESSAGE);
                return true;
            }
            
            if(getServer().getPlayer(channelPlayer) == null) {
                sendErrorMessage(sender, "Player '" + channelPlayer + "' not found");
                return true;
            }

            dataOutput.writeBoolean(true);
            messageSender = getServer().getPlayer(channelPlayer);
        }

        dataOutput.writeUTF(args[1]);
        dataOutput.writeUTF(Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length)));
        dataOutput.writeBoolean(sender.isOp());
        messageSender.sendPluginMessage(this, MESSAGE_CHANNEL, dataOutput.toByteArray());

        return true;
    }

    /**
     * Print an error message
     *
     * @param sender  Sender that execute the current command
     * @param message Message to send to command sender
     */
    private void sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(this.PLUGIN_NAME_PREFIX + ChatColor.RED + message);
    }
}

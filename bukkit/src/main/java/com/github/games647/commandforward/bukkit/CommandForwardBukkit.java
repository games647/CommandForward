package com.github.games647.commandforward.bukkit;

import com.github.games647.commandforward.bukkit.command.ForwardCommand;
import com.github.games647.commandforward.bukkit.command.InterceptCommand;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandForwardBukkit extends JavaPlugin {

    private static final String MESSAGE_CHANNEL = "commandforward:cmd";

    private final String prefix = ChatColor.DARK_AQUA + "["
            + ChatColor.GOLD + this.getName()
            + ChatColor.DARK_AQUA + "] ";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, MESSAGE_CHANNEL);

        CommandExecutor forwardCommand = new ForwardCommand(this, MESSAGE_CHANNEL, prefix);
        Optional.ofNullable(getCommand("forward")).ifPresent(cmd -> cmd.setExecutor(forwardCommand));

        CommandExecutor interceptCommand = new InterceptCommand(this, MESSAGE_CHANNEL, prefix);
        Optional.ofNullable(getCommand("intercept")).ifPresent(cmd -> cmd.setExecutor(interceptCommand));
    }
}

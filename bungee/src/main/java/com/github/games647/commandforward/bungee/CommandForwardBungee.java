package com.github.games647.commandforward.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;

public class CommandForwardBungee extends Plugin implements Listener {

    private static final String MESSAGE_CHANNEL = "commandforward:cmd";

    @Override
    public void onEnable() {
        //this is required to listen to messages from the server
        getProxy().registerChannel(MESSAGE_CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onServerConnected(PluginMessageEvent messageEvent) {
        String channel = messageEvent.getTag();
        if (messageEvent.isCancelled() || !Objects.equals(MESSAGE_CHANNEL, channel)) {
            return;
        }

        messageEvent.setCancelled(true);

        //check if the message is sent from the server
        if (Server.class.isAssignableFrom(messageEvent.getSender().getClass())) {
            parseMessage((CommandSender) messageEvent.getReceiver(), ByteStreams.newDataInput(messageEvent.getData()));
        }
    }

    private void parseMessage(CommandSender sender, ByteArrayDataInput dataInput) {
        boolean isPlayer = dataInput.readBoolean();
        String command = dataInput.readUTF();
        String arguments = dataInput.readUTF();

        CommandSender invoker = getProxy().getConsole();
        if (isPlayer) {
            invoker = sender;
        }

        invokeCommand(invoker, dataInput.readBoolean(), command, arguments);
    }

    private void invokeCommand(CommandSender invoker, boolean isOp, String command, String arguments) {
        PluginManager pluginManager = getProxy().getPluginManager();
        //if (isOp) { // TODO: Make OP Configurable and Fix command map!!!
        if (false) {
            try {
                // TODO: Fix using the command map! https://github.com/games647/CommandForward/issues/3
                Map<String, Command> commandMap = (Map<String, Command>) pluginManager.getClass()
                        .getField("commandMap").get(pluginManager);

                Command pluginCmd = commandMap.get(command);
                if (pluginCmd == null) {
                    invoker.sendMessage(new ComponentBuilder("[CommandForward] Command '" + command + "' not found")
                            .color(ChatColor.RED)
                            .create());
                } else {
                    pluginCmd.execute(invoker, arguments.split(" "));
                }
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                String exMess = ex.getMessage();
                BaseComponent[] message = new ComponentBuilder("[CommandForward]  Error occurred executing command '" + exMess + "'")
                        .color(ChatColor.RED)
                        .create();
                invoker.sendMessage(message);
                getLogger().log(Level.WARNING, "[CommandForward]  Cannot access command map for executing command", ex);
            }
        } else {
            String commandLine = command + ' ' + arguments;
            if(!pluginManager.dispatchCommand(invoker, commandLine)) {
              invoker.sendMessage(new ComponentBuilder("[CommandForward] Command '" + command + "' not found")
                      .color(ChatColor.RED)
                      .create());
            }
        }
    }
}

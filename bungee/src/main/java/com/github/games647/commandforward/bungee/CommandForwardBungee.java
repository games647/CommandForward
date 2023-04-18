package com.github.games647.commandforward.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

        // do not forward it further to the client
        messageEvent.setCancelled(true);

        //check if the message is sent from the server
        if (ProxiedPlayer.class.isAssignableFrom(messageEvent.getReceiver().getClass())) {
            executeMessage(
                    (ProxiedPlayer) messageEvent.getReceiver(),
                    ByteStreams.newDataInput(messageEvent.getData())
            );
        }
    }

    private void executeMessage(ProxiedPlayer sender, ByteArrayDataInput dataInput) {
        boolean isPlayer = dataInput.readBoolean();
        String command = dataInput.readUTF().toLowerCase();
        String arguments = dataInput.readUTF();
        CommandSender invoker = (isPlayer) ? sender : getProxy().getConsole();
        boolean isOp = dataInput.readBoolean();

        Optional<Command> optCmd = getRegisteredCommand(command);
        if (optCmd.isEmpty()) {
            if (isPlayer) {
                // execute only player commands if Bukkit requested to use this player as the receiver
                ChatEvent event = new ChatEvent(sender, sender, '/' + command + ' ' + arguments);
                getProxy().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    sendErrorMessage(invoker, "Unknown player command: " + command);
                }
            } else {
                sendErrorMessage(invoker, "Unknown console command: " + command);
            }
        } else {
            Command cmd = optCmd.get();
            boolean isDisabled = !getProxy().getPluginManager().isExecutableCommand(command, invoker);
            if (isDisabled) {
                sendErrorMessage(invoker, "Command is disabled");
                return;
            }

            if (isOp) {
                // skip additional permission checks - the permissions will be checked on Bukkit
                cmd.execute(invoker, arguments.split(" "));
            } else {
                getProxy().getPluginManager().dispatchCommand(invoker, command + ' ' + arguments);
            }
        }
    }

    private Optional<Command> getRegisteredCommand(String command) {
        return getProxy().getPluginManager().getCommands()
                .stream()
                .filter(entry -> entry.getKey().equals(command))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    /**
     * Print an error message
     *
     * @param sender  Sender that execute the current command
     * @param message Message to send to command sender
     */
    private void sendErrorMessage(CommandSender sender, String message) {
        String prefix = String.format("[%s] %s", getDescription().getName(), message);
        BaseComponent[] components = new ComponentBuilder(prefix)
                .color(ChatColor.RED)
                .append(message)
                .create();
        sender.sendMessage(components);
    }
}

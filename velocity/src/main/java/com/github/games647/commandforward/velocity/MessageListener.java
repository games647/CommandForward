package com.github.games647.commandforward.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

public class MessageListener {

    private final ProxyServer server;
    private final Logger logger;
    private final ChannelIdentifier identifier;

    public MessageListener(ProxyServer server, Logger logger, ChannelIdentifier identifier) {
        this.server = server;
        this.logger = logger;
        this.identifier = identifier;
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        // Received plugin message, check channel identifier matches
        if (event.getIdentifier().equals(identifier)) {
            // Since this message was meant for this listener set it to handled
            // We do this so the message doesn't get routed through.
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            if (event.getSource() instanceof ServerConnection) {
                // Read the data written to the message
                Player player = ((ServerConnection) event.getSource()).getPlayer();
                ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
                parseMessage(player, in);
            }
        }
    }

    private void parseMessage(Player source, ByteArrayDataInput dataInput) {
        boolean isPlayer = dataInput.readBoolean();
        String command = dataInput.readUTF();
        String arguments = dataInput.readUTF();
        CommandSource invoker = (isPlayer) ? source : server.getConsoleCommandSource();

        String username = source.getUsername();
        logger.info("Received new forward command '{}' with Args: '{}' from {}", command, arguments, username);

        invokeCommand(invoker, dataInput.readBoolean(), command, arguments);
    }

    private void invokeCommand(CommandSource invoker, boolean isOp, String command, String arguments) {
        PluginManager pluginManager = server.getPluginManager();
        CommandManager commandManager = server.getCommandManager();

        // TODO implement isOp handle progress
        commandManager.executeImmediatelyAsync(invoker, command + ' ' + arguments)
                .thenAccept(success -> {
                    if (!success) {
                        sendErrorMessage(invoker, "Failed to find command");
                    }
                })
                .exceptionally(error -> {
                    logger.warn("Failed to invoke forwarded command", error);
                    return null;
                });
    }

    /**
     * Print an error message
     *
     * @param source  Sender that execute the current command
     * @param message Message to send to command sender
     */
    private void sendErrorMessage(Audience source, String message) {
        String msg = String.format("[%s] %s", "CommandForward", message);
        Component textComponent = Component.text(msg, NamedTextColor.RED);
        source.sendMessage(textComponent);
    }
}

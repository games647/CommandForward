package com.github.games647.commandforward.velocity;

import com.github.games647.commandforward.velocity.MessageListener;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Plugin(
    id = "commandforward",
    name = "CommandForward",
    version = "0.4.0",
    description = "Forwards commands from Bukkit to BungeeCord (Or Velocity) to execute it there",
    authors = {"games647", "https://github.com/games647/CommandForward/graphs/contributors"}
)
public class CommandForwardVelocity {

    private final ChannelIdentifier MESSAGE_CHANNEL = MinecraftChannelIdentifier.from("commandforward:cmd");

    private static Optional<CommandForwardVelocity> instance;
    private final ProxyServer proxyServer;
    private final Logger logger;

    private final List<RegisteredServer> lobbies = new ArrayList<>();
    private final List<RegisteredServer> bedwars = new ArrayList<>();

    @Inject
    public CommandForwardVelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        instance = Optional.of(this);
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        // Register the custom messaging channel
        proxyServer.getChannelRegistrar().register(MESSAGE_CHANNEL);
        // Register an event handler to catch messages for it
        proxyServer.getEventManager().register(this, new MessageListener(MESSAGE_CHANNEL));

    }

    /**
     * Print an error message
     *
     * @param source  Sender that execute the current command
     * @param message Message to send to command sender
     */
    private void sendErrorMessage(CommandSource source, String message) {
        Component textComponent = Component.text(String.format("[%s] %s", "CommandForward", message), NamedTextColor.RED);
        source.sendMessage(textComponent);
    }

    public static CommandForwardVelocity getInstance() {
        return instance.orElseThrow(IllegalAccessError::new);
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }
}

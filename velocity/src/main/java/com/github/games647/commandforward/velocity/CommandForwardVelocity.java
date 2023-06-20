package com.github.games647.commandforward.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

/**
 *
 * Velocity support for CommandForward plugin
 * Since Velocity supports BungeeCord plugin-
 * messaging channels now
 *
 * @author  Alijk
 * @since   2022-02-13
 *
 */
@Plugin(
    id = "commandforward",
    name = "CommandForward",
    version = "0.5.0",
    description = "Forwards commands from Bukkit to BungeeCord (Or Velocity) to execute it there",
    authors = {"games647", "https://github.com/games647/CommandForward/graphs/contributors"}
)
public class CommandForwardVelocity {

    private final ChannelIdentifier MESSAGE_CHANNEL = MinecraftChannelIdentifier.from("commandforward:cmd");

    private final ProxyServer proxyServer;
    private final Logger logger;

    @Inject
    public CommandForwardVelocity(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        // Register the custom messaging channel
        proxyServer.getChannelRegistrar().register(MESSAGE_CHANNEL);

        // Register an event handler to catch messages for it
        proxyServer.getEventManager().register(this, new MessageListener(proxyServer, logger, MESSAGE_CHANNEL));
    }
}

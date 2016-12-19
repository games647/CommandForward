package com.github.games647.commandforward.bungee;

import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class CommandForwardBungee extends Plugin implements Listener {

    private static final boolean REMOVE_DUPLICATES = false;

    private final ConcurrentMap<String, Object> prevCommands = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .<String, Object>build().asMap();


    @Override
    public void onEnable() {
        //this is required to listen to messages from the server
        getProxy().registerChannel(getDescription().getName());
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onServerConnected(PluginMessageEvent messageEvent) {
        String channel = messageEvent.getTag();
        if (messageEvent.isCancelled() || !Objects.equals(getDescription().getName(), channel)) {
            return;
        }

        messageEvent.setCancelled(true);

        //check if the message is sent from the server
        if (Server.class.isAssignableFrom(messageEvent.getSender().getClass())) {
            ByteArrayDataInput dataInput = ByteStreams.newDataInput(messageEvent.getData());
            boolean isPlayer = dataInput.readBoolean();
            String command = dataInput.readUTF();
            String arguments = dataInput.readUTF();

            ProxiedPlayer invoker = (ProxiedPlayer) messageEvent.getReceiver();
            String commandLine = command + ' ' + arguments;
            if (REMOVE_DUPLICATES && prevCommands.containsKey(commandLine)) {
                return;
            }

            prevCommands.put(commandLine, new Object());

            if (isPlayer) {
                //the proxied player is the actual invoker other it's the console
                getProxy().getPluginManager().dispatchCommand(invoker, commandLine);
            } else {
                CommandSender console = getProxy().getConsole();
                getProxy().getPluginManager().dispatchCommand(console, commandLine);
            }
        }
    }
}

package com.github.games647.commandforward.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;

public class MessageListener {
    private final ChannelIdentifier identifier;

    public MessageListener(ChannelIdentifier identifier){
        this.identifier = identifier;
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event){
        // Received plugin message, check channel identifier matches
        if(event.getIdentifier().equals(identifier)){
            // Since this message was meant for this listener set it to handled
            // We do this so the message doesn't get routed through.
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            if(event.getSource() instanceof ServerConnection){
                // Read the data written to the message
                Player p = ((ServerConnection) event.getSource()).getPlayer();
                ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
                parseMessage(p, in);
            }
        }
    }


    private void parseMessage(CommandSource source, ByteArrayDataInput dataInput) {
        final boolean isPlayer = dataInput.readBoolean();
        final String command = dataInput.readUTF();
        final String arguments = dataInput.readUTF();
        final CommandSource invoker = (isPlayer) ? source : CommandForwardVelocity.getInstance().getProxyServer().getConsoleCommandSource();

        invokeCommand(invoker, dataInput.readBoolean(), command, arguments);
    }

    private void invokeCommand(CommandSource invoker, boolean isOp, String command, String arguments) {
        PluginManager pluginManager = CommandForwardVelocity.getInstance().getProxyServer().getPluginManager();
        CommandManager commandManager = CommandForwardVelocity.getInstance().getProxyServer().getCommandManager();

        // TODO complete isOp progression
        commandManager.executeAsync(invoker, command + " " + arguments);
    }


}

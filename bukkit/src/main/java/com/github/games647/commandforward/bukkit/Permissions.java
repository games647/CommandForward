package com.github.games647.commandforward.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public enum Permissions {
    FORWARD("commandforward.bukkit.command.forward"),
    FORWARD_CONSOLE("commandforward.bukkit.command.forward.console"),
    FORWARD_OTHER("commandforward.bukkit.command.forward.other");

    private final String permission;
    public static final String ERROR_MESSAGE = "You are not allowed to execute this command";

    Permissions(String perm) {
        this.permission = perm;
    }

    /**
     * Define if the permission is set
     *
     * @param player Player on which check the permissions
     * @return Return a boolean to define if the permission is set
     */
    public boolean isSetOn(Permissible player) {
        return player != null && (!(player instanceof Player) || player.hasPermission(this.permission));
    }
}

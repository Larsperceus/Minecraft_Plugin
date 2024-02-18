package com.lars.rpg_plugin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerClassManager {
    private final Map<UUID, String> playerClasses = new HashMap<>();

    // Assign a class to a player
    public void setPlayerClass(Player player, String className) {
        playerClasses.put(player.getUniqueId(), className);
        System.out.println("Assigned class " + className + " to player " + player.getName());
    }

    // Get the class of a player
    public String getPlayerClass(Player player) {
        return playerClasses.getOrDefault(player.getUniqueId(), "");
    }

    // Check if a player is of a specific class
    public boolean isPlayerClass(Player player, String className) {
        return className.equalsIgnoreCase(getPlayerClass(player));
    }

    // Optional: Remove a player's class (e.g., on disconnect or class change)
    public void removePlayerClass(Player player) {
        playerClasses.remove(player.getUniqueId());
    }
}

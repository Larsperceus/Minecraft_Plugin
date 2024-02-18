package com.lars.rpg_plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class RPGPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PlayerClassManager playerClassManager = new PlayerClassManager();
        // Plugin startup logic
        getLogger().info("RPG_Plugin is enabled!");
        Objects.requireNonNull(getCommand("menu")).setExecutor(new ClassCommand());
        Objects.requireNonNull(getCommand("skills")).setExecutor(new SkillsCommand(playerClassManager));


        getServer().getPluginManager().registerEvents(new ClassListener(playerClassManager), this);
        getServer().getPluginManager().registerEvents(new SkillUpgradeListener(this, playerClassManager), this);
        new AbilityListener(this, playerClassManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("RPG_Plugin is disabled.");
    }
}


package com.lars.rpg_plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillUpgradeListener implements Listener {
    private final Map<UUID, Map<String, Integer>> playerSkills = new HashMap<>();
    private final Map<UUID, Long> lastUpgradeTime = new HashMap<>();
    private final int MAX_SKILL_LEVEL = 10;
    private static final long UPGRADE_COOLDOWN_MS = 1000; // 1 second cooldown

    private final JavaPlugin plugin;
    private final PlayerClassManager playerClassManager;
    private File skillsFile;
    private FileConfiguration skillsConfig;

    public SkillUpgradeListener(JavaPlugin plugin, PlayerClassManager playerClassManager) {
        this.plugin = plugin;
        this.playerClassManager = playerClassManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        createSkillsFile();
        loadPlayerSkills();
    }

    private void createSkillsFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        skillsFile = new File(plugin.getDataFolder(), "skills.yml");
        if (!skillsFile.exists()) {
            try {
                skillsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        skillsConfig = YamlConfiguration.loadConfiguration(skillsFile);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        String title = ChatColor.translateAlternateColorCodes('&', event.getView().getTitle());
        if (title.equals(ChatColor.DARK_PURPLE + "Warrior Skills")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String skillName = ChatColor.stripColor(itemMeta.getDisplayName()).split(" LVL:")[0];
                    upgradeSkill(player, skillName);
                }
            }
        }
    }
    private void upgradeSkill(Player player, String skillName) {
        UUID playerId = player.getUniqueId();
        playerSkills.putIfAbsent(playerId, new HashMap<>());
        Map<String, Integer> skills = playerSkills.get(playerId);

        long currentTime = System.currentTimeMillis();
        Long lastUpgrade = lastUpgradeTime.getOrDefault(playerId, 0L);
        if (currentTime - lastUpgrade < UPGRADE_COOLDOWN_MS) {
            return; // Ignore the click if it's within the cooldown period
        }
        lastUpgradeTime.put(playerId, currentTime); // Update the last upgrade time

        int currentLevel = skills.getOrDefault(skillName, 0);
        if (currentLevel < MAX_SKILL_LEVEL) {
            skills.put(skillName, currentLevel + 1);
            player.sendMessage(ChatColor.GREEN + "Upgraded " + skillName + " to level " + (currentLevel + 1) + "!");
            celebrateLevelUp(player);

            savePlayerSkills(); // Save after upgrading
        } else {
            player.sendMessage(ChatColor.RED + "Your " + skillName + " is already at maximum level!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }
    private void celebrateLevelUp(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 30, 0.5, 0.5, 0.5, 0);
            }
        }.runTaskLater(plugin, 20L);
    }
    public void savePlayerSkills() {
        for (UUID playerId : playerSkills.keySet()) {
            Map<String, Integer> classSkills = playerSkills.get(playerId);
            for (String classSkill : classSkills.keySet()) {
                String[] parts = classSkill.split("\\."); // Split "Class.Ability"
                String className = parts[0];
                String skillName = parts[1];
                int level = classSkills.get(classSkill);
                skillsConfig.set(playerId.toString() + "." + className + "." + skillName, level);
            }
        }
        try {
            skillsConfig.save(skillsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadPlayerSkills() {
        for (String playerId : skillsConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(playerId);
            Map<String, Integer> classSkills = new HashMap<>();
            for (String className : skillsConfig.getConfigurationSection(playerId).getKeys(false)) {
                for (String skill : skillsConfig.getConfigurationSection(playerId + "." + className).getKeys(false)) {
                    int level = skillsConfig.getInt(playerId + "." + className + "." + skill);
                    classSkills.put(className + "." + skill, level); // Store as "Class.Ability"
                }
            }
            playerSkills.put(uuid, classSkills);
        }
    }
}

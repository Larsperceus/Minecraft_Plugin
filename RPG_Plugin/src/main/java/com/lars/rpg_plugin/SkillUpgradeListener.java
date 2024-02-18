package com.lars.rpg_plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillUpgradeListener implements Listener {
    private final Map<UUID, Map<String, Integer>> playerSkills = new HashMap<>();
    private final int MAX_SKILL_LEVEL = 10;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Skills")) {
            event.setCancelled(true); // Prevent taking items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta.hasDisplayName()) {
                    String skillName = ChatColor.stripColor(meta.getDisplayName());
                    upgradeSkill(player, skillName);
                }
            }
        }
    }

    private void upgradeSkill(Player player, String skillName) {
        Map<String, Integer> skills = playerSkills.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        int currentLevel = skills.getOrDefault(skillName, 0);
        if (currentLevel < MAX_SKILL_LEVEL) {
            skills.put(skillName, currentLevel + 1);
            player.sendMessage(ChatColor.GREEN + "Upgraded " + skillName + " to level " + (currentLevel + 1) + "!");
            updateSkillsInventory(player);
        } else {
            player.sendMessage(ChatColor.RED + "Your " + skillName + " is already at maximum level!");
        }
    }

    private void updateSkillsInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Your Skills");

        // Populate the inventory with skills, now including levels
        Map<String, Integer> skills = playerSkills.getOrDefault(player.getUniqueId(), new HashMap<>());

        // Example adding the Armor Upgrade skill with level display
        String armorSkill = "Armor Upgrade";
        int armorLevel = skills.getOrDefault(armorSkill, 1);
        inv.setItem(10, createSkillItem(Material.IRON_CHESTPLATE, armorSkill + " LVL: " + armorLevel, "Upgrade your armor for better protection.", armorLevel));

        // Repeat for other skills...

        player.openInventory(inv);
    }

    private ItemStack createSkillItem(Material material, String name, String description, int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + name);
            meta.setLore(Arrays.asList(ChatColor.GRAY + description, ChatColor.GRAY + "Level: " + level + "/" + MAX_SKILL_LEVEL));
            item.setItemMeta(meta);
        }
        return item;
    }
}

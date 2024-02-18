package com.lars.rpg_plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class SkillsCommand implements CommandExecutor {

    private final PlayerClassManager playerClassManager;

    public SkillsCommand(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openSkillsMenu(player);
        }
        return true;
    }

    private void openSkillsMenu(Player player) {
        // Determine the player's class and open the appropriate skills menu
        String playerClass = playerClassManager.getPlayerClass(player);
        switch (playerClass) {
            case "Warrior":
                openWarriorSkillsMenu(player);
                break;
            // Add cases for other classes
            default:
                player.sendMessage(ChatColor.RED + "You have not selected a class yet!");
                break;
        }
    }

    private void openWarriorSkillsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Warrior Skills");

        // Add skill items to the inventory
        // For example, a sword for Armor Upgrade, a shield for Shield Upgrade, etc.
        inv.setItem(10, createSkillItem(Material.IRON_CHESTPLATE, "Armor Upgrade", "Upgrade your armor for better protection."));
        inv.setItem(12, createSkillItem(Material.SHIELD, "Shield Upgrade", "Enhance your shield to withstand more damage."));
        inv.setItem(14, createSkillItem(Material.ENCHANTED_BOOK, "Special Skill", "Enhance your special skill for greater effect."));
        inv.setItem(16, createSkillItem(Material.APPLE, "Health Increase", "Increase your maximum health."));

        player.openInventory(inv);
    }

    private ItemStack createSkillItem(Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + name);
            meta.setLore(Arrays.asList(ChatColor.GRAY + description));
            item.setItemMeta(meta);
        }
        return item;
    }
}

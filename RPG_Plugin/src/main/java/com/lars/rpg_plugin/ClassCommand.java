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

public class ClassCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Inventory inv = Bukkit.createInventory(player, 27, ChatColor.GOLD + "Kit Selector");

            // Warrior Kit GUI
            ItemStack warrior = new ItemStack(Material.IRON_SWORD);
            ItemMeta warriorMeta = warrior.getItemMeta();
            if (warriorMeta != null) {
                warriorMeta.setDisplayName(ChatColor.AQUA + "Warrior Kit");
                warriorMeta.setLore(Arrays.asList(ChatColor.GRAY + "Grants the Warrior kit."));
                warrior.setItemMeta(warriorMeta);
            }

            // Mage Kit GUI
            ItemStack mage = new ItemStack(Material.BLAZE_ROD);
            ItemMeta mageMeta = mage.getItemMeta();
            if (mageMeta != null) {
                mageMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mage Kit");
                mageMeta.setLore(Arrays.asList(ChatColor.GRAY + "Grants the Mage kit."));
                mage.setItemMeta(mageMeta);
            }

            // Archer Kit GUI
            ItemStack archer = new ItemStack(Material.BOW);
            ItemMeta archerMeta = archer.getItemMeta();
            if (archerMeta != null) {
                archerMeta.setDisplayName(ChatColor.GREEN + "Archer Kit");
                archerMeta.setLore(Arrays.asList(ChatColor.GRAY + "Grants the Archer kit."));
                archer.setItemMeta(archerMeta);
            }

            // Rogue Kit GUI
            ItemStack rogue = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
            ItemMeta rogueMeta = rogue.getItemMeta();
            if (rogueMeta != null) {
                rogueMeta.setDisplayName(ChatColor.DARK_GRAY + "Rogue Kit");
                rogueMeta.setLore(Arrays.asList(ChatColor.GRAY + "Grants the Rogue kit."));
                rogue.setItemMeta(rogueMeta);
            }

            inv.setItem(10, warrior);
            inv.setItem(12, mage);
            inv.setItem(14, archer);
            inv.setItem(16, rogue);

            player.openInventory(inv);
        }
        return true;
    }
}
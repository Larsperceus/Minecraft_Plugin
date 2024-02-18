package com.lars.rpg_plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ClassListener implements Listener {
    private final PlayerClassManager playerClassManager;
    public ClassListener(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = ChatColor.translateAlternateColorCodes('&', e.getView().getTitle());
        if (title.equals(ChatColor.GOLD + "Kit Selector")) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                Player player = (Player) e.getWhoClicked();
                String kitName = "";

                switch (e.getRawSlot()) {
                    case 10: kitName = "Warrior"; break;
                    case 12: kitName = "Mage"; break;
                    case 14: kitName = "Archer"; break;
                    case 16: kitName = "Rogue"; break;
                    default: // No action for other slots
                }

                if (!kitName.isEmpty()) {
                    giveKitItems(player, kitName);
                    playerClassManager.setPlayerClass(player, kitName); // Set the player's class
                    player.sendMessage(ChatColor.GRAY + "You've selected the " + kitName + " kit.");
                }
                player.closeInventory();
            }
        }
    }

    private void giveKitItems(Player player, String kitName) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear(); // Optionally clear inventory before giving new items

        switch (kitName) {
            case "Warrior":
                inventory.addItem(new ItemStack(Material.IRON_SWORD), new ItemStack(Material.SHIELD));
                break;
            case "Mage":
                inventory.addItem(new ItemStack(Material.BLAZE_ROD), new ItemStack(Material.ENCHANTED_BOOK));
                break;
            case "Archer":
                inventory.addItem(new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 64));
                break;
            case "Rogue":
                inventory.addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.STONE_SWORD));
                break;
        }
    }
}

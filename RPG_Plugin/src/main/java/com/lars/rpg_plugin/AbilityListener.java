package com.lars.rpg_plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityListener implements Listener {
    private final Map<UUID, Long> shieldBashCooldowns = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> playerSkills = new HashMap<>(); // Stores skill levels for each player
    private final PlayerClassManager playerClassManager;
    private final JavaPlugin plugin;

    public AbilityListener(JavaPlugin plugin, PlayerClassManager playerClassManager) {
        this.plugin = plugin;
        this.playerClassManager = playerClassManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null && event.getItem().getType() == Material.SHIELD) {
            if (playerClassManager.isPlayerClass(player, "Warrior") && canUseShieldBash(player)) {
                int skillLevel = getSkillLevel(player, "Shield Bash");
                performShieldBash(player, skillLevel);
                shieldBashCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                player.sendMessage("§cYou must be a Warrior to use Shield Bash!");
            }
        }
    }

    private boolean canUseShieldBash(Player player) {
        long SHIELD_BASH_COOLDOWN = 20 * 1000; // 20 seconds in milliseconds
        Long lastUse = shieldBashCooldowns.get(player.getUniqueId());
        if (lastUse == null) return true;

        long timePassed = System.currentTimeMillis() - lastUse;
        if (timePassed < SHIELD_BASH_COOLDOWN) {
            long timeLeft = (SHIELD_BASH_COOLDOWN - timePassed) / 1000;
            player.sendMessage("§cShield Bash is on cooldown. Please wait " + timeLeft + " seconds.");
            return false;
        }
        return true;
    }

    private void performShieldBash(Player player, int skillLevel) {
        // Modify effects based on skill level
        double dashMultiplier = 1.0 + (0.1 * skillLevel); // Increase dash speed by 10% per skill level
        double knockbackStrength = 1.0 + (0.1 * skillLevel); // Increase knockback by 10% per skill level
        double damage = 5.0 + (skillLevel); // Increase damage by 1 per skill level

        Vector direction = player.getLocation().getDirection().multiply(dashMultiplier);
        direction.setY(0); // Prevents the dash from going upwards or downwards
        player.setVelocity(direction);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            boolean hasHit = false;
            for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
                if (entity instanceof LivingEntity && !entity.equals(player)) {
                    LivingEntity target = (LivingEntity) entity;
                    Vector knockbackDir = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    target.setVelocity(knockbackDir.multiply(knockbackStrength));
                    target.damage(damage, player); // Apply damage based on skill level
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100)); // Stun effect
                    hasHit = true;
                }
            }

            if (hasHit) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 10);
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 50);
            }
        }, 10L);

        player.sendMessage("§aYou performed a Shield Bash!");
    }

    private int getSkillLevel(Player player, String skill) {
        return playerSkills.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).getOrDefault(skill, 1); // Default skill level is 1
    }
}

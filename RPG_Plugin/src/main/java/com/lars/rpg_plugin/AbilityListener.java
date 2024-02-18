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
                performShieldBash(player);
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

    private void performShieldBash(Player player) {
        // Dash effect
        Vector direction = player.getLocation().getDirection().multiply(1.5); // Adjust the multiplier to control dash speed
        direction.setY(0); // Prevents the dash from going upwards or downwards
        player.setVelocity(direction);

        // Delay the knockback effect to synchronize with the dash
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            boolean hasHit = false;
            for (Entity entity : player.getNearbyEntities(2, 2, 2)) { // Check entities in a small radius around the player after dashing
                if (entity instanceof LivingEntity && !entity.equals(player)) {
                    LivingEntity target = (LivingEntity) entity;
                    Vector knockbackDirection = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    double knockbackStrength = 1.5; // Adjust as needed
                    target.setVelocity(knockbackDirection.multiply(knockbackStrength));
                    target.damage(6.0, player); // Apply damage
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 100)); // Stun effect for 2 seconds
                    hasHit = true;
                }
            }

            if (hasHit) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 10);
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 50); // Additional crit particles for visual effect
            }
        }, 10L); // Delay in ticks (10 ticks = 1/2 second, adjust as needed)

        player.sendMessage("§aYou performed a Shield Bash!");
    }
}

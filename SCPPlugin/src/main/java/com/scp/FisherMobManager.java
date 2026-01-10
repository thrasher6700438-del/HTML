package com.scp;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class FisherMobManager implements Listener {
    
    private final SCPPlugin plugin;
    private final Random random = new Random();
    private final List<Entity> activeFishers = new ArrayList<>();
    
    public FisherMobManager(SCPPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void spawnFishers(World world) {
        // Spawn 2 fishers per chunk in loaded chunks
        for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
            for (int i = 0; i < 2; i++) {
                spawnFisherInChunk(chunk);
            }
        }
    }
    
    private void spawnFisherInChunk(org.bukkit.Chunk chunk) {
        // Find a suitable spawn location
        int x = chunk.getX() * 16 + random.nextInt(16);
        int z = chunk.getZ() * 16 + random.nextInt(16);
        int y = chunk.getWorld().getHighestBlockYAt(x, z);
        
        Location spawnLoc = new Location(chunk.getWorld(), x, y + 1, z);
        
        // Spawn Fisher (using Drowned as base)
        Drowned fisher = (Drowned) chunk.getWorld().spawnEntity(spawnLoc, EntityType.DROWNED);
        setupFisher(fisher);
        activeFishers.add(fisher);
    }
    
    private void setupFisher(Drowned fisher) {
        // Mark as Fisher
        fisher.setMetadata("scp_fisher", new FixedMetadataValue(plugin, true));
        
        // Set custom properties
        fisher.setCustomName("§3§lThe Fisher");
        fisher.setCustomNameVisible(true);
        
        // Enhanced stats
        fisher.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60.0);
        fisher.setHealth(60.0);
        fisher.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
        fisher.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
        
        // Special effects
        fisher.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
        fisher.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        fisher.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
        
        // Give fishing rod
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
        fisher.getEquipment().setItemInMainHand(fishingRod);
        fisher.getEquipment().setItemInMainHandDropChance(0.1f);
        
        // Spawn effects
        fisher.getWorld().spawnParticle(Particle.WATER_BUBBLE, fisher.getLocation(), 30, 2, 2, 2, 0.1);
        fisher.getWorld().playSound(fisher.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 1.0f, 0.8f);
        
        // Start Fisher behavior
        startFisherBehavior(fisher);
    }
    
    private void startFisherBehavior(Drowned fisher) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (fisher.isDead() || !fisher.isValid()) return;
            
            // Spawn water particles around Fisher
            fisher.getWorld().spawnParticle(Particle.DRIP_WATER, 
                fisher.getLocation().add(0, 1, 0), 5, 1, 1, 1, 0.1);
            
            // Special attack - hook nearby players
            for (Entity nearby : fisher.getNearbyEntities(8, 8, 8)) {
                if (nearby instanceof Player && random.nextDouble() < 0.05) { // 5% chance per tick
                    Player player = (Player) nearby;
                    hookPlayer(fisher, player);
                }
            }
        }, 0L, 20L);
    }
    
    private void hookPlayer(Drowned fisher, Player player) {
        // Pull player towards Fisher
        Location fisherLoc = fisher.getLocation();
        Location playerLoc = player.getLocation();
        
        double distance = fisherLoc.distance(playerLoc);
        if (distance > 1) {
            org.bukkit.util.Vector direction = fisherLoc.toVector().subtract(playerLoc.toVector()).normalize();
            player.setVelocity(direction.multiply(0.8));
            
            player.sendMessage("§3§lThe Fisher has hooked you!");
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0f, 1.0f);
            
            // Add slowness effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
        }
    }
    
    @EventHandler
    public void onFisherDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata("scp_fisher")) {
            // Special drops
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.FISHING_ROD));
            event.getDrops().add(new ItemStack(Material.COD, random.nextInt(3) + 1));
            event.getDrops().add(new ItemStack(Material.PRISMARINE_SHARD, random.nextInt(2) + 1));
            
            // Rare drops
            if (random.nextDouble() < 0.1) { // 10% chance
                event.getDrops().add(new ItemStack(Material.HEART_OF_THE_SEA));
            }
            
            event.setDroppedExp(50);
            
            // Death effects
            Location loc = event.getEntity().getLocation();
            loc.getWorld().spawnParticle(Particle.WATER_SPLASH, loc, 50, 3, 3, 3, 0.2);
            loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.0f, 0.8f);
            
            activeFishers.remove(event.getEntity());
        }
    }
    
    public void removeFishers(World world) {
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : world.getEntities()) {
            if (entity.hasMetadata("scp_fisher")) {
                // Despawn effects
                entity.getWorld().spawnParticle(Particle.WATER_BUBBLE, entity.getLocation(), 20, 1, 1, 1, 0.1);
                entity.remove();
                toRemove.add(entity);
            }
        }
        activeFishers.removeAll(toRemove);
    }
    
    public List<Entity> getActiveFishers() {
        return new ArrayList<>(activeFishers);
    }
}
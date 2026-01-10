package com.scp;

import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;
import java.util.HashMap;
import java.util.Map;

public class SCPMobManager implements Listener {
    
    private final SCPPlugin plugin;
    private final Map<EntityType, Double> mobHealthMap = new HashMap<>();
    
    public SCPMobManager(SCPPlugin plugin) {
        this.plugin = plugin;
        initializeMobHealth();
    }
    
    private void initializeMobHealth() {
        mobHealthMap.put(EntityType.ZOMBIE, 40.0);
        mobHealthMap.put(EntityType.SKELETON, 35.0);
        mobHealthMap.put(EntityType.CREEPER, 45.0);
        mobHealthMap.put(EntityType.SPIDER, 30.0);
        mobHealthMap.put(EntityType.ENDERMAN, 80.0);
        mobHealthMap.put(EntityType.WITCH, 50.0);
        mobHealthMap.put(EntityType.PILLAGER, 45.0);
        mobHealthMap.put(EntityType.VINDICATOR, 55.0);
        mobHealthMap.put(EntityType.EVOKER, 60.0);
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!plugin.getMoonEventManager().isFullMoonActive()) return;
        
        Entity entity = event.getEntity();
        if (entity instanceof Monster) {
            transformToSCPVariant((Monster) entity);
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("scp_variant")) {
            // SCP variants deal 2 hearts (4 damage)
            event.setDamage(4.0);
            
            // Add fear effect to victim
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0));
                player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 10);
            }
        }
    }
    
    public void transformAllMobs(World world) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Monster && !entity.hasMetadata("scp_variant")) {
                transformToSCPVariant((Monster) entity);
            }
        }
    }
    
    public void revertAllMobs(World world) {
        for (Entity entity : world.getEntities()) {
            if (entity.hasMetadata("scp_variant")) {
                revertSCPVariant((Monster) entity);
            }
        }
    }
    
    private void transformToSCPVariant(Monster mob) {
        // Mark as SCP variant
        mob.setMetadata("scp_variant", new FixedMetadataValue(plugin, true));
        
        // Set custom health
        Double customHealth = mobHealthMap.get(mob.getType());
        if (customHealth != null) {
            mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(customHealth);
            mob.setHealth(customHealth);
        }
        
        // Add SCP effects
        mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
        
        // Set custom name
        mob.setCustomName("§4§l[SCP] §c" + getScpName(mob.getType()));
        mob.setCustomNameVisible(true);
        
        // Visual effects
        mob.getWorld().spawnParticle(Particle.SMOKE_LARGE, mob.getLocation(), 20, 1, 1, 1, 0.1);
        mob.getWorld().spawnParticle(Particle.REDSTONE, mob.getLocation(), 30, 1, 1, 1, 0.1);
    }
    
    private void revertSCPVariant(Monster mob) {
        // Remove SCP metadata
        mob.removeMetadata("scp_variant", plugin);
        
        // Reset health to default
        EntityType type = mob.getType();
        double defaultHealth = getDefaultHealth(type);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(defaultHealth);
        mob.setHealth(Math.min(mob.getHealth(), defaultHealth));
        
        // Remove effects
        mob.removePotionEffect(PotionEffectType.SPEED);
        mob.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        mob.removePotionEffect(PotionEffectType.GLOWING);
        
        // Reset name
        mob.setCustomName(null);
        mob.setCustomNameVisible(false);
    }
    
    private String getScpName(EntityType type) {
        switch (type) {
            case ZOMBIE: return "Wish I Knew - Walker";
            case SKELETON: return "Wish I Knew - Archer";
            case CREEPER: return "Wish I Knew - Explosive";
            case SPIDER: return "Wish I Knew - Crawler";
            case ENDERMAN: return "Wish I Knew - Teleporter";
            case WITCH: return "Wish I Knew - Caster";
            case PILLAGER: return "Wish I Knew - Raider";
            case VINDICATOR: return "Wish I Knew - Berserker";
            case EVOKER: return "Wish I Knew - Summoner";
            default: return "Wish I Knew - Unknown";
        }
    }
    
    private double getDefaultHealth(EntityType type) {
        switch (type) {
            case ZOMBIE: return 20.0;
            case SKELETON: return 20.0;
            case CREEPER: return 20.0;
            case SPIDER: return 16.0;
            case ENDERMAN: return 40.0;
            case WITCH: return 26.0;
            case PILLAGER: return 24.0;
            case VINDICATOR: return 24.0;
            case EVOKER: return 24.0;
            default: return 20.0;
        }
    }
}
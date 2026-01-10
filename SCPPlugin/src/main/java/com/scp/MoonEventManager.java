package com.scp;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import java.util.Random;

public class MoonEventManager implements Listener {
    
    private final SCPPlugin plugin;
    private final Random random = new Random();
    private boolean fullMoonActive = false;
    private boolean halfMoonActive = false;
    private boolean fullMoonForced = false;
    private boolean halfMoonForced = false;
    private boolean fullMoonDisabled = false;
    private boolean halfMoonDisabled = false;
    
    public MoonEventManager(SCPPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void checkMoonPhase(World world) {
        if (world.getTime() < 13000 || world.getTime() > 23000) {
            // Not night time
            if (fullMoonActive && !fullMoonForced) {
                deactivateFullMoon(world);
            }
            if (halfMoonActive && !halfMoonForced) {
                deactivateHalfMoon(world);
            }
            return;
        }
        
        // Night time - check moon phases
        long fullTime = world.getFullTime();
        int moonPhase = (int) ((fullTime / 24000) % 8);
        
        // Full moon (phase 0) with small chance
        if (moonPhase == 0 && !fullMoonDisabled) {
            if (!fullMoonActive && (fullMoonForced || random.nextDouble() < 0.15)) { // 15% chance
                activateFullMoon(world);
            }
        }
        
        // Half moon (phases 2 and 6) with 25% chance
        if ((moonPhase == 2 || moonPhase == 6) && !halfMoonDisabled) {
            if (!halfMoonActive && (halfMoonForced || random.nextDouble() < 0.25)) { // 25% chance
                activateHalfMoon(world);
            }
        }
    }
    
    private void activateFullMoon(World world) {
        fullMoonActive = true;
        
        // Change sky to black and white neon
        for (Player player : world.getPlayers()) {
            player.sendTitle("§4§lSCP BREACH", "§cWish I Knew has awakened...", 10, 60, 20);
            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f);
        }
        
        // Transform existing mobs
        plugin.getMobManager().transformAllMobs(world);
        
        // Start atmospheric effects
        startFullMoonEffects(world);
        
        Bukkit.getLogger().info("[SCP] Full moon SCP event activated!");
    }
    
    private void activateHalfMoon(World world) {
        halfMoonActive = true;
        
        for (Player player : world.getPlayers()) {
            player.sendTitle("§6§lFISHER MOON", "§eThe Fisher emerges...", 10, 40, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 1.0f, 0.8f);
        }
        
        // Spawn Fisher mobs
        plugin.getFisherManager().spawnFishers(world);
        
        Bukkit.getLogger().info("[SCP] Half moon Fisher event activated!");
    }
    
    private void deactivateFullMoon(World world) {
        fullMoonActive = false;
        fullMoonForced = false;
        
        // Revert mobs
        plugin.getMobManager().revertAllMobs(world);
        
        for (Player player : world.getPlayers()) {
            player.sendTitle("§a§lSAFE", "§7The nightmare ends...", 10, 40, 20);
        }
        
        Bukkit.getLogger().info("[SCP] Full moon event deactivated!");
    }
    
    private void deactivateHalfMoon(World world) {
        halfMoonActive = false;
        halfMoonForced = false;
        
        // Remove Fisher mobs
        plugin.getFisherManager().removeFishers(world);
        
        for (Player player : world.getPlayers()) {
            player.sendTitle("§b§lCALM", "§7The waters are still...", 10, 40, 20);
        }
        
        Bukkit.getLogger().info("[SCP] Half moon event deactivated!");
    }
    
    private void startFullMoonEffects(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fullMoonActive) {
                    cancel();
                    return;
                }
                
                // Spawn eerie particles
                for (Player player : world.getPlayers()) {
                    player.spawnParticle(Particle.SMOKE_LARGE, 
                        player.getLocation().add(random.nextInt(20) - 10, 10, random.nextInt(20) - 10), 
                        5, 0.5, 0.5, 0.5, 0.01);
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }
    
    // Force methods
    public void forceFullMoon() {
        fullMoonForced = true;
        fullMoonDisabled = false;
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                activateFullMoon(world);
            }
        }
    }
    
    public void forceHalfMoon() {
        halfMoonForced = true;
        halfMoonDisabled = false;
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                activateHalfMoon(world);
            }
        }
    }
    
    public void disableFullMoon() {
        fullMoonDisabled = true;
        fullMoonForced = false;
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                deactivateFullMoon(world);
            }
        }
    }
    
    public void disableHalfMoon() {
        halfMoonDisabled = true;
        halfMoonForced = false;
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                deactivateHalfMoon(world);
            }
        }
    }
    
    public void disableAllEvents() {
        disableFullMoon();
        disableHalfMoon();
    }
    
    // Getters
    public boolean isFullMoonActive() { return fullMoonActive; }
    public boolean isHalfMoonActive() { return halfMoonActive; }
}
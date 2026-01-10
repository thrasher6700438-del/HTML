package com.scp;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.ArrayList;

public class LagOptimizer {
    
    private final SCPPlugin plugin;
    private BukkitRunnable optimizationTask;
    private boolean isRunning = false;
    
    // Configuration
    private int maxEntitiesPerChunk = 50;
    private int maxItemsPerChunk = 30;
    private int maxExpOrbsPerChunk = 20;
    private long itemDespawnTime = 6000; // 5 minutes in ticks
    
    public LagOptimizer(SCPPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void startOptimization() {
        if (isRunning) return;
        
        isRunning = true;
        optimizationTask = new BukkitRunnable() {
            @Override
            public void run() {
                optimizeServer();
            }
        };
        
        // Run every 30 seconds
        optimizationTask.runTaskTimer(plugin, 600L, 600L);
        
        plugin.getLogger().info("[SCP] Lag optimizer started!");
    }
    
    public void stopOptimization() {
        if (optimizationTask != null) {
            optimizationTask.cancel();
            isRunning = false;
        }
        plugin.getLogger().info("[SCP] Lag optimizer stopped!");
    }
    
    private void optimizeServer() {
        int entitiesRemoved = 0;
        int itemsRemoved = 0;
        int expOrbsRemoved = 0;
        
        for (World world : Bukkit.getWorlds()) {
            for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                // Optimize entities in chunk
                Entity[] entities = chunk.getEntities();
                
                // Count different entity types
                List<Item> items = new ArrayList<>();
                List<ExperienceOrb> expOrbs = new ArrayList<>();
                int totalEntities = 0;
                
                for (Entity entity : entities) {
                    if (entity instanceof Item) {
                        items.add((Item) entity);
                    } else if (entity instanceof ExperienceOrb) {
                        expOrbs.add((ExperienceOrb) entity);
                    }
                    totalEntities++;
                }
                
                // Remove excess items
                if (items.size() > maxItemsPerChunk) {
                    // Remove oldest items first
                    items.sort((a, b) -> Integer.compare(a.getTicksLived(), b.getTicksLived()));
                    for (int i = maxItemsPerChunk; i < items.size(); i++) {
                        items.get(i).remove();
                        itemsRemoved++;
                    }
                }
                
                // Remove old items
                for (Item item : items) {
                    if (item.getTicksLived() > itemDespawnTime) {
                        item.remove();
                        itemsRemoved++;
                    }
                }
                
                // Remove excess experience orbs
                if (expOrbs.size() > maxExpOrbsPerChunk) {
                    // Merge experience orbs
                    int totalExp = 0;
                    for (ExperienceOrb orb : expOrbs) {
                        totalExp += orb.getExperience();
                        orb.remove();
                        expOrbsRemoved++;
                    }
                    
                    // Spawn one large orb
                    if (totalExp > 0 && !expOrbs.isEmpty()) {
                        world.spawn(expOrbs.get(0).getLocation(), ExperienceOrb.class)
                             .setExperience(Math.min(totalExp, 1000)); // Cap at 1000 exp
                    }
                }
                
                // Remove excess entities if chunk is overloaded
                if (totalEntities > maxEntitiesPerChunk) {
                    int toRemove = totalEntities - maxEntitiesPerChunk;
                    for (Entity entity : entities) {
                        if (toRemove <= 0) break;
                        
                        // Don't remove players, SCP entities, or named entities
                        if (entity instanceof org.bukkit.entity.Player ||
                            entity.hasMetadata("scp_variant") ||
                            entity.hasMetadata("scp_fisher") ||
                            entity.getCustomName() != null) {
                            continue;
                        }
                        
                        entity.remove();
                        entitiesRemoved++;
                        toRemove--;
                    }
                }
            }
        }
        
        // Log optimization results if significant
        if (entitiesRemoved > 0 || itemsRemoved > 0 || expOrbsRemoved > 0) {
            plugin.getLogger().info(String.format(
                "[SCP] Lag optimization: Removed %d entities, %d items, %d exp orbs",
                entitiesRemoved, itemsRemoved, expOrbsRemoved
            ));
        }
        
        // Force garbage collection if needed
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        if (usedMemory > maxMemory * 0.8) { // If using more than 80% memory
            System.gc();
            plugin.getLogger().info("[SCP] Forced garbage collection due to high memory usage");
        }
    }
    
    // Configuration methods
    public void setMaxEntitiesPerChunk(int max) { this.maxEntitiesPerChunk = max; }
    public void setMaxItemsPerChunk(int max) { this.maxItemsPerChunk = max; }
    public void setMaxExpOrbsPerChunk(int max) { this.maxExpOrbsPerChunk = max; }
    public void setItemDespawnTime(long ticks) { this.itemDespawnTime = ticks; }
    
    // Getters
    public boolean isRunning() { return isRunning; }
    public int getMaxEntitiesPerChunk() { return maxEntitiesPerChunk; }
    public int getMaxItemsPerChunk() { return maxItemsPerChunk; }
    public int getMaxExpOrbsPerChunk() { return maxExpOrbsPerChunk; }
    public long getItemDespawnTime() { return itemDespawnTime; }
}
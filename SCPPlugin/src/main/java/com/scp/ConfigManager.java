package com.scp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    private final SCPPlugin plugin;
    private File configFile;
    private FileConfiguration config;
    
    public ConfigManager(SCPPlugin plugin) {
        this.plugin = plugin;
        setupConfig();
    }
    
    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();
    }
    
    private void setDefaults() {
        // Moon event settings
        config.addDefault("moon-events.full-moon.chance", 0.15);
        config.addDefault("moon-events.full-moon.enabled", true);
        config.addDefault("moon-events.half-moon.chance", 0.25);
        config.addDefault("moon-events.half-moon.enabled", true);
        
        // SCP mob settings
        config.addDefault("scp-mobs.damage", 4.0);
        config.addDefault("scp-mobs.health-multiplier", 2.0);
        config.addDefault("scp-mobs.speed-boost", 1);
        config.addDefault("scp-mobs.resistance", 0);
        
        // Fisher settings
        config.addDefault("fisher.spawn-count-per-chunk", 2);
        config.addDefault("fisher.health", 60.0);
        config.addDefault("fisher.damage", 8.0);
        config.addDefault("fisher.hook-range", 8.0);
        config.addDefault("fisher.hook-chance", 0.05);
        
        // Lag optimizer settings
        config.addDefault("lag-optimizer.enabled", true);
        config.addDefault("lag-optimizer.max-entities-per-chunk", 50);
        config.addDefault("lag-optimizer.max-items-per-chunk", 30);
        config.addDefault("lag-optimizer.max-exp-orbs-per-chunk", 20);
        config.addDefault("lag-optimizer.item-despawn-time", 6000);
        config.addDefault("lag-optimizer.check-interval", 600);
        
        config.options().copyDefaults(true);
        saveConfig();
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config file: " + e.getMessage());
        }
    }
    
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    // Getters for moon events
    public double getFullMoonChance() { return config.getDouble("moon-events.full-moon.chance"); }
    public boolean isFullMoonEnabled() { return config.getBoolean("moon-events.full-moon.enabled"); }
    public double getHalfMoonChance() { return config.getDouble("moon-events.half-moon.chance"); }
    public boolean isHalfMoonEnabled() { return config.getBoolean("moon-events.half-moon.enabled"); }
    
    // Getters for SCP mobs
    public double getSCPMobDamage() { return config.getDouble("scp-mobs.damage"); }
    public double getSCPMobHealthMultiplier() { return config.getDouble("scp-mobs.health-multiplier"); }
    public int getSCPMobSpeedBoost() { return config.getInt("scp-mobs.speed-boost"); }
    public int getSCPMobResistance() { return config.getInt("scp-mobs.resistance"); }
    
    // Getters for Fisher
    public int getFisherSpawnCount() { return config.getInt("fisher.spawn-count-per-chunk"); }
    public double getFisherHealth() { return config.getDouble("fisher.health"); }
    public double getFisherDamage() { return config.getDouble("fisher.damage"); }
    public double getFisherHookRange() { return config.getDouble("fisher.hook-range"); }
    public double getFisherHookChance() { return config.getDouble("fisher.hook-chance"); }
    
    // Getters for lag optimizer
    public boolean isLagOptimizerEnabled() { return config.getBoolean("lag-optimizer.enabled"); }
    public int getMaxEntitiesPerChunk() { return config.getInt("lag-optimizer.max-entities-per-chunk"); }
    public int getMaxItemsPerChunk() { return config.getInt("lag-optimizer.max-items-per-chunk"); }
    public int getMaxExpOrbsPerChunk() { return config.getInt("lag-optimizer.max-exp-orbs-per-chunk"); }
    public long getItemDespawnTime() { return config.getLong("lag-optimizer.item-despawn-time"); }
    public long getCheckInterval() { return config.getLong("lag-optimizer.check-interval"); }
}
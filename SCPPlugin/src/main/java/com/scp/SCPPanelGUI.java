package com.scp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

public class SCPPanelGUI implements Listener {
    
    private final SCPPlugin plugin;
    private final String PANEL_TITLE = "§4§lSCP Management Panel";
    
    public SCPPanelGUI(SCPPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openPanel(Player player) {
        Inventory panel = Bukkit.createInventory(null, 54, PANEL_TITLE);
        
        // Moon Event Controls
        panel.setItem(10, createItem(Material.CLOCK, "§6§lMoon Events", Arrays.asList(
            "§7Manage moon-based SCP events",
            "",
            "§aFull Moon: " + (plugin.getMoonEventManager().isFullMoonActive() ? "§2Active" : "§cInactive"),
            "§aHalf Moon: " + (plugin.getMoonEventManager().isHalfMoonActive() ? "§2Active" : "§cInactive"),
            "",
            "§eClick to manage moon events"
        )));
        
        // SCP Mob Management
        panel.setItem(12, createItem(Material.ZOMBIE_HEAD, "§4§lSCP Mobs", Arrays.asList(
            "§7Manage SCP mob variants",
            "",
            "§cWish I Knew variants deal 2 hearts damage",
            "§cEnhanced health and abilities",
            "",
            "§eClick to manage SCP mobs"
        )));
        
        // Fisher Management
        panel.setItem(14, createItem(Material.FISHING_ROD, "§3§lThe Fisher", Arrays.asList(
            "§7Manage Fisher mob spawning",
            "",
            "§bSpawns during half moon events",
            "§b2 Fishers per chunk",
            "§bSpecial hook abilities",
            "",
            "§eClick to manage Fisher"
        )));
        
        // Lag Optimizer
        panel.setItem(16, createItem(Material.REDSTONE, "§a§lLag Optimizer", Arrays.asList(
            "§7Server performance optimization",
            "",
            "§aStatus: " + (plugin.getLagOptimizer().isRunning() ? "§2Running" : "§cStopped"),
            "§aMax Entities/Chunk: §f" + plugin.getLagOptimizer().getMaxEntitiesPerChunk(),
            "§aMax Items/Chunk: §f" + plugin.getLagOptimizer().getMaxItemsPerChunk(),
            "",
            "§eClick to configure optimizer"
        )));
        
        // Quick Actions
        panel.setItem(28, createItem(Material.ENDER_EYE, "§5§lForce Full Moon", Arrays.asList(
            "§7Instantly activate full moon event",
            "",
            "§cTransforms all mobs to SCP variants",
            "§cActivates atmospheric effects",
            "",
            "§eClick to force activate"
        )));
        
        panel.setItem(30, createItem(Material.PRISMARINE_SHARD, "§b§lForce Half Moon", Arrays.asList(
            "§7Instantly activate half moon event",
            "",
            "§3Spawns Fisher mobs",
            "§3Activates water effects",
            "",
            "§eClick to force activate"
        )));
        
        panel.setItem(32, createItem(Material.BARRIER, "§c§lDisable All Events", Arrays.asList(
            "§7Stop all active SCP events",
            "",
            "§cReverts all SCP mobs",
            "§cRemoves all Fishers",
            "§cStops all effects",
            "",
            "§eClick to disable all"
        )));
        
        panel.setItem(34, createItem(Material.BOOK, "§e§lServer Statistics", Arrays.asList(
            "§7View server performance stats",
            "",
            "§aTPS: §f" + getServerTPS(),
            "§aMemory Usage: §f" + getMemoryUsage() + "%",
            "§aLoaded Chunks: §f" + getLoadedChunks(),
            "",
            "§eClick for detailed stats"
        )));
        
        // Configuration
        panel.setItem(40, createItem(Material.WRITABLE_BOOK, "§6§lConfiguration", Arrays.asList(
            "§7Plugin configuration settings",
            "",
            "§6Event chances and timings",
            "§6Mob health and damage values",
            "§6Optimization parameters",
            "",
            "§eClick to configure"
        )));
        
        // Info
        panel.setItem(44, createItem(Material.PAPER, "§f§lPlugin Info", Arrays.asList(
            "§7SCP Plugin v1.0.0",
            "",
            "§bFeatures:",
            "§7• Moon-based SCP events",
            "§7• Custom SCP mob variants",
            "§7• Fisher mob spawning",
            "§7• Advanced lag optimization",
            "",
            "§aPlugin by YourName"
        )));
        
        // Fill empty slots with glass panes
        for (int i = 0; i < 54; i++) {
            if (panel.getItem(i) == null) {
                panel.setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, " ", Arrays.asList()));
            }
        }
        
        player.openInventory(panel);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(PANEL_TITLE)) return;
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        
        switch (event.getSlot()) {
            case 10: // Moon Events
                openMoonEventPanel(player);
                break;
            case 12: // SCP Mobs
                openSCPMobPanel(player);
                break;
            case 14: // Fisher
                openFisherPanel(player);
                break;
            case 16: // Lag Optimizer
                openOptimizerPanel(player);
                break;
            case 28: // Force Full Moon
                plugin.getMoonEventManager().forceFullMoon();
                player.sendMessage("§a§lFull moon event activated!");
                player.closeInventory();
                break;
            case 30: // Force Half Moon
                plugin.getMoonEventManager().forceHalfMoon();
                player.sendMessage("§a§lHalf moon event activated!");
                player.closeInventory();
                break;
            case 32: // Disable All
                plugin.getMoonEventManager().disableAllEvents();
                player.sendMessage("§c§lAll SCP events disabled!");
                player.closeInventory();
                break;
            case 34: // Statistics
                openStatsPanel(player);
                break;
            case 40: // Configuration
                openConfigPanel(player);
                break;
            case 44: // Info
                player.sendMessage("§a§lSCP Plugin v1.0.0 - Advanced SCP events and optimization");
                break;
        }
    }
    
    private void openMoonEventPanel(Player player) {
        // Sub-panel for moon events
        Inventory moonPanel = Bukkit.createInventory(null, 27, "§6§lMoon Event Management");
        
        moonPanel.setItem(11, createItem(Material.CLOCK, "§4§lFull Moon Event", Arrays.asList(
            "§7Status: " + (plugin.getMoonEventManager().isFullMoonActive() ? "§2Active" : "§cInactive"),
            "",
            "§cTransforms mobs to SCP variants",
            "§cDeals 2 hearts damage",
            "§c15% chance during full moon",
            "",
            "§eLeft click: Force activate",
            "§eRight click: Disable"
        )));
        
        moonPanel.setItem(15, createItem(Material.PRISMARINE_SHARD, "§3§lHalf Moon Event", Arrays.asList(
            "§7Status: " + (plugin.getMoonEventManager().isHalfMoonActive() ? "§2Active" : "§cInactive"),
            "",
            "§3Spawns Fisher mobs",
            "§325% chance during half moon",
            "§32 Fishers per chunk",
            "",
            "§eLeft click: Force activate",
            "§eRight click: Disable"
        )));
        
        moonPanel.setItem(22, createItem(Material.ARROW, "§f§lBack to Main Panel", Arrays.asList()));
        
        player.openInventory(moonPanel);
    }
    
    private void openOptimizerPanel(Player player) {
        // Sub-panel for lag optimizer
        Inventory optimizerPanel = Bukkit.createInventory(null, 27, "§a§lLag Optimizer Settings");
        
        optimizerPanel.setItem(10, createItem(Material.GREEN_WOOL, "§a§lStart Optimizer", Arrays.asList(
            "§7Start the lag optimization system",
            "",
            "§aOptimizes entity counts",
            "§aRemoves excess items",
            "§aMerges experience orbs",
            "",
            "§eClick to start"
        )));
        
        optimizerPanel.setItem(12, createItem(Material.RED_WOOL, "§c§lStop Optimizer", Arrays.asList(
            "§7Stop the lag optimization system",
            "",
            "§cDisables automatic cleanup",
            "",
            "§eClick to stop"
        )));
        
        optimizerPanel.setItem(14, createItem(Material.COMPARATOR, "§6§lOptimizer Settings", Arrays.asList(
            "§7Current settings:",
            "",
            "§6Max Entities/Chunk: §f" + plugin.getLagOptimizer().getMaxEntitiesPerChunk(),
            "§6Max Items/Chunk: §f" + plugin.getLagOptimizer().getMaxItemsPerChunk(),
            "§6Max Exp Orbs/Chunk: §f" + plugin.getLagOptimizer().getMaxExpOrbsPerChunk(),
            "",
            "§eClick to modify settings"
        )));
        
        optimizerPanel.setItem(22, createItem(Material.ARROW, "§f§lBack to Main Panel", Arrays.asList()));
        
        player.openInventory(optimizerPanel);
    }
    
    private void openStatsPanel(Player player) {
        player.sendMessage("§a§l=== SCP Plugin Statistics ===");
        player.sendMessage("§eTPS: §f" + getServerTPS());
        player.sendMessage("§eMemory Usage: §f" + getMemoryUsage() + "%");
        player.sendMessage("§eLoaded Chunks: §f" + getLoadedChunks());
        player.sendMessage("§eFull Moon Active: §f" + plugin.getMoonEventManager().isFullMoonActive());
        player.sendMessage("§eHalf Moon Active: §f" + plugin.getMoonEventManager().isHalfMoonActive());
        player.sendMessage("§eActive Fishers: §f" + plugin.getFisherManager().getActiveFishers().size());
        player.sendMessage("§eLag Optimizer: §f" + (plugin.getLagOptimizer().isRunning() ? "Running" : "Stopped"));
        player.closeInventory();
    }
    
    private void openSCPMobPanel(Player player) {
        player.sendMessage("§4§l=== SCP Mob Information ===");
        player.sendMessage("§cSCP variants are created during full moon events");
        player.sendMessage("§cAll variants deal 2 hearts (4 damage)");
        player.sendMessage("§cEnhanced health, speed, and resistance");
        player.sendMessage("§cGlowing effect for visibility");
        player.closeInventory();
    }
    
    private void openFisherPanel(Player player) {
        player.sendMessage("§3§l=== Fisher Information ===");
        player.sendMessage("§bSpawns during half moon events (25% chance)");
        player.sendMessage("§b2 Fishers spawn per loaded chunk");
        player.sendMessage("§bCan hook players from 8 blocks away");
        player.sendMessage("§bDrops fishing rods and sea items");
        player.sendMessage("§bActive Fishers: " + plugin.getFisherManager().getActiveFishers().size());
        player.closeInventory();
    }
    
    private void openConfigPanel(Player player) {
        player.sendMessage("§6§l=== Configuration ===");
        player.sendMessage("§eConfiguration files are located in plugins/SCPPlugin/");
        player.sendMessage("§eRestart server after making changes");
        player.closeInventory();
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private String getServerTPS() {
        try {
            Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            double[] tps = (double[]) server.getClass().getField("recentTps").get(server);
            return String.format("%.2f", tps[0]);
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private int getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        return (int) ((used * 100) / max);
    }
    
    private int getLoadedChunks() {
        int total = 0;
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            total += world.getLoadedChunks().length;
        }
        return total;
    }
}
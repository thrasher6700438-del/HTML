package com.scp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class SCPPlugin extends JavaPlugin implements Listener {
    
    private MoonEventManager moonEventManager;
    private SCPMobManager mobManager;
    private FisherMobManager fisherManager;
    private LagOptimizer lagOptimizer;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        // Initialize managers
        configManager = new ConfigManager(this);
        moonEventManager = new MoonEventManager(this);
        mobManager = new SCPMobManager(this);
        fisherManager = new FisherMobManager(this);
        lagOptimizer = new LagOptimizer(this);
        
        // Register events
        getServer().getPluginManager().registerEvents(moonEventManager, this);
        getServer().getPluginManager().registerEvents(mobManager, this);
        getServer().getPluginManager().registerEvents(fisherManager, this);
        getServer().getPluginManager().registerEvents(new SCPPanelGUI(this), this);
        
        // Start moon checker task
        startMoonChecker();
        
        // Start lag optimizer
        lagOptimizer.startOptimization();
        
        getLogger().info("SCP Plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (lagOptimizer != null) {
            lagOptimizer.stopOptimization();
        }
        getLogger().info("SCP Plugin has been disabled!");
    }
    
    private void startMoonChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        moonEventManager.checkMoonPhase(world);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 200L); // Check every 10 seconds
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("scp")) {
            return handleSCPCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("scppanel")) {
            return handlePanelCommand(sender);
        }
        return false;
    }
    
    private boolean handleSCPCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("scp.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /scp <halfmoon|fullmoon|disable>");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "fullmoon":
                moonEventManager.forceFullMoon();
                sender.sendMessage("§aForced full moon event activated!");
                break;
            case "halfmoon":
                moonEventManager.forceHalfMoon();
                sender.sendMessage("§aForced half moon event activated!");
                break;
            case "disable":
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("fullmoon")) {
                        moonEventManager.disableFullMoon();
                        sender.sendMessage("§aFull moon event disabled!");
                    } else if (args[1].equalsIgnoreCase("halfmoon")) {
                        moonEventManager.disableHalfMoon();
                        sender.sendMessage("§aHalf moon event disabled!");
                    }
                } else {
                    moonEventManager.disableAllEvents();
                    sender.sendMessage("§aAll SCP events disabled!");
                }
                break;
            default:
                sender.sendMessage("§cInvalid argument! Use: halfmoon, fullmoon, or disable");
        }
        return true;
    }
    
    private boolean handlePanelCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        if (!sender.hasPermission("scp.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        new SCPPanelGUI(this).openPanel(player);
        return true;
    }
    
    // Getters
    public MoonEventManager getMoonEventManager() { return moonEventManager; }
    public SCPMobManager getMobManager() { return mobManager; }
    public FisherMobManager getFisherManager() { return fisherManager; }
    public LagOptimizer getLagOptimizer() { return lagOptimizer; }
    public ConfigManager getConfigManager() { return configManager; }
}
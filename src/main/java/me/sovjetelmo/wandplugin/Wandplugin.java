package me.sovjetelmo.wandplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Wandplugin extends JavaPlugin {
    private static Wandplugin instance;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this; // Set the instance when the plugin is enabled
        this.guiManager = new GUIManager(this);
        getCommand("magicwand").setExecutor(new Commands());
        getCommand("mw").setExecutor(this);
        getCommand("mwconfig").setExecutor(this);
        getServer().getPluginManager().registerEvents(new MagicWandsListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("mw")) {
            guiManager.openWandSelectionGUI(player);
            return true;
        } else if (command.getName().equalsIgnoreCase("mwconfig")) {
            guiManager.openSpellConfigGUI(player);
            return true;
        }

        return false;
    }

    public static Wandplugin getInstance() {
        return instance;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }
}

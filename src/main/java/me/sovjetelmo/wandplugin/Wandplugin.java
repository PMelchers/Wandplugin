package me.sovjetelmo.wandplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Wandplugin extends JavaPlugin {
    private static Wandplugin instance;
    private GUIManager guiManager;
    private ConfigVars configVars;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("spells.yml", false);
        configVars = new ConfigVars();
        guiManager = new GUIManager(this, configVars);
        getCommand("magicwand").setExecutor(new Commands(this, guiManager));
        getCommand("mw").setExecutor(new Commands(this, guiManager));
        getCommand("mwconfig").setExecutor(new Commands(this, guiManager));
        getServer().getPluginManager().registerEvents(new MagicWandsListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    public static Wandplugin getInstance() {
        return instance;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public ConfigVars getConfigVars() {
        return configVars;
    }
}
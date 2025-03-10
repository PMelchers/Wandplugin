package me.sovjetelmo.wandplugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigVars {
    private final Wandplugin plugin = Wandplugin.getInstance();
    private FileConfiguration spellsConfig;
    private final File spellsFile;

    public ConfigVars() {
        spellsFile = new File(plugin.getDataFolder(), "spells.yml");
        loadConfig();
    }

    private void loadConfig() {
        if (!spellsFile.exists()) {
            plugin.saveResource("spells.yml", false);
        }
        spellsConfig = YamlConfiguration.loadConfiguration(spellsFile);
    }

    public void saveConfig() {
        try {
            spellsConfig.save(spellsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getSpellProperty(String wandType, String spellName, String property) {
        return spellsConfig.getDouble(wandType + "." + spellName + "." + property);
    }

    public void setSpellProperty(String wandType, String spellName, String property, double value) {
        if (spellsConfig.contains(wandType + "." + spellName)) {
            spellsConfig.set(wandType + "." + spellName + "." + property, value);
        }
        saveConfig();
    }

    public List<String> getSpells(String wandType) {
        List<String> spells = new ArrayList<>();
        ConfigurationSection section = spellsConfig.getConfigurationSection(wandType);
        if (section != null) {
            spells.addAll(section.getKeys(false));
        }
        return spells;
    }
}
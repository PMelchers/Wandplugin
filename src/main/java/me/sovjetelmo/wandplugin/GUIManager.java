package me.sovjetelmo.wandplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private final Wandplugin plugin;
    private final WandItem wandItem;
    private final Map<String, Map<String, Map<String, Double>>> spellConfigs;

    public GUIManager(Wandplugin plugin) {
        this.plugin = plugin;
        this.spellConfigs = new HashMap<>();
        this.wandItem = new WandItem();
        initializeSpellConfigs();
    }

    private void initializeSpellConfigs() {
        // Initialize with default values
        Map<String, Map<String, Double>> godSpells = new HashMap<>();
        godSpells.put("Regeneration", createSpellConfig(5.0, 10.0));
        godSpells.put("Healing Aura", createSpellConfig(3.0, 15.0));
        // Add more God wand spells...

        Map<String, Map<String, Double>> raftagarSpells = new HashMap<>();
        raftagarSpells.put("Fireball", createSpellConfig(2.0, 5.0));
        raftagarSpells.put("Lightning Strike", createSpellConfig(1.0, 3.0));
        // Add more Raftagar wand spells...

        Map<String, Map<String, Double>> empireSpells = new HashMap<>();
        empireSpells.put("Tornado", createSpellConfig(4.0, 8.0));
        empireSpells.put("Potion", createSpellConfig(3.0, 6.0));
        // Add more Empire wand spells...

        spellConfigs.put("god", godSpells);
        spellConfigs.put("raftagar", raftagarSpells);
        spellConfigs.put("empire", empireSpells);
    }

    private Map<String, Double> createSpellConfig(double radius, double duration) {
        Map<String, Double> config = new HashMap<>();
        config.put("radius", radius);
        config.put("duration", duration);
        return config;
    }

    public void openWandSelectionGUI(Player player) {
        // Create a new inventory with 9 slots for the GUI
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Select Your Wand");

        // Use WandItem to create wand items for each type
        ItemStack godWand = wandItem.createWandItem("god");
        ItemStack raftagarWand = wandItem.createWandItem("raftagar");
        ItemStack empireWand = wandItem.createWandItem("empire");

        // Add the wand items to the GUI
        gui.setItem(0, godWand);
        gui.setItem(1, raftagarWand);
        gui.setItem(2, empireWand);

        // Open the GUI for the player
        player.openInventory(gui);
    }

    public void openSpellConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Wand Selection");

        gui.setItem(11, createGuiItem(Material.GOLD_NUGGET, ChatColor.AQUA + "God Wand"));
        gui.setItem(13, createGuiItem(Material.STICK, ChatColor.BLACK + "Raftagar Wand"));
        gui.setItem(15, createGuiItem(Material.BLAZE_ROD, ChatColor.DARK_PURPLE + "Empire Wand"));

        player.openInventory(gui);
    }

    public void openSpellListGUI(Player player, String wandType) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.GREEN + wandType + " Spells");

        Map<String, Map<String, Double>> spells = spellConfigs.get(wandType.toLowerCase());
        int slot = 0;
        for (String spellName : spells.keySet()) {
            gui.setItem(slot, createGuiItem(Material.BOOK, ChatColor.YELLOW + spellName));
            slot++;
        }

        player.openInventory(gui);
    }

    public void openSpellConfigurationGUI(Player player, String wandType, String spellName) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + spellName + " Configuration");

        Map<String, Double> config = spellConfigs.get(wandType.toLowerCase()).get(spellName);

        gui.setItem(11, createGuiItem(Material.BEACON, ChatColor.AQUA + "Radius: " + config.get("radius"),
                ChatColor.GRAY + "Click to modify"));
        gui.setItem(15, createGuiItem(Material.CLOCK, ChatColor.AQUA + "Duration: " + config.get("duration"),
                ChatColor.GRAY + "Click to modify"));

        player.openInventory(gui);
    }

    public void updateSpellConfig(String wandType, String spellName, String variable, double value) {
        spellConfigs.get(wandType.toLowerCase()).get(spellName).put(variable, value);
    }

    public double getSpellConfig(String wandType, String spellName, String variable) {
        return spellConfigs.get(wandType.toLowerCase()).get(spellName).get(variable);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}

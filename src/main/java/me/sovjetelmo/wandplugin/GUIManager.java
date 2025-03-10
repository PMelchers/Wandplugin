package me.sovjetelmo.wandplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GUIManager {
    private final ConfigVars configVars;

    public GUIManager(Wandplugin plugin, ConfigVars configVars) {
        this.configVars = configVars;
    }

    public void openWandSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Wand Selection");

        gui.setItem(11, createGuiItem(Material.GOLD_NUGGET, ChatColor.AQUA + "God Wand"));
        gui.setItem(13, createGuiItem(Material.STICK, ChatColor.BLACK + "Raftagar Wand"));
        gui.setItem(15, createGuiItem(Material.BLAZE_ROD, ChatColor.DARK_PURPLE + "Empire Wand"));

        player.openInventory(gui);
    }

    public void openSpellListGUI(Player player, String wandType) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GREEN + wandType + " Spells");

        for (String spellName : configVars.getSpells(wandType)) {
            gui.addItem(createGuiItem(Material.ENCHANTED_BOOK, ChatColor.GOLD + spellName));
        }

        player.openInventory(gui);
    }

    public void openSpellConfigurationGUI(Player player, String wandType, String spellName) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + spellName + " Configuration");

        double radius = configVars.getSpellProperty(wandType, spellName, "radius");
        double duration = configVars.getSpellProperty(wandType, spellName, "duration");

        gui.setItem(11, createGuiItem(Material.BEACON, ChatColor.AQUA + "Radius: " + radius, ChatColor.GRAY + "Click to modify"));
        gui.setItem(15, createGuiItem(Material.CLOCK, ChatColor.AQUA + "Duration: " + duration, ChatColor.GRAY + "Click to modify"));

        player.openInventory(gui);
    }

    public void updateSpellConfig(String wandType, String spellName, String variable, double value) {
        configVars.setSpellProperty(wandType, spellName, variable, value);
        Player player = Bukkit.getPlayer("playerName");
        if (player != null) {
            openSpellConfigurationGUI(player, wandType, spellName);
        }
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
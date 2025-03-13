package me.sovjetelmo.wandplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class GUIManager {
    private final ConfigVars configVars;

    public GUIManager(Wandplugin plugin, ConfigVars configVars) {
        this.configVars = configVars;
    }

    public void openSpellConfigurationGUI(Player player, String wandType, String spellName) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + spellName + " Configuration");

        double radius = configVars.getSpellProperty(wandType, spellName, "radius");
        double duration = configVars.getSpellProperty(wandType, spellName, "duration");
        double cooldown = configVars.getSpellProperty(wandType, spellName, "cooldown");

        gui.setItem(11, createGuiItem(Material.BEACON, ChatColor.AQUA + "Radius: " + radius, ChatColor.GRAY + "Click to modify"));
        gui.setItem(13, createGuiItem(Material.CLOCK, ChatColor.AQUA + "Duration: " + duration, ChatColor.GRAY + "Click to modify"));
        gui.setItem(15, createGuiItem(Material.REDSTONE, ChatColor.AQUA + "Cooldown: " + cooldown, ChatColor.GRAY + "Click to modify"));

        player.openInventory(gui);
    }

    public void openWandSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Wand Selection");

        gui.setItem(11, createGuiItem(Material.GOLD_NUGGET, ChatColor.AQUA + ChatColor.BOLD.toString() + "God Wand", ChatColor.GRAY + "Click to select"));
        gui.setItem(13, createGuiItem(Material.STICK, ChatColor.BLACK + ChatColor.BOLD.toString() + "Raftagar Wand", ChatColor.GRAY + "Click to select"));
        gui.setItem(15, createGuiItem(Material.BLAZE_ROD, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Empire Wand", ChatColor.GRAY + "Click to select"));

        player.openInventory(gui);
    }

    public void openSpellListGUI(Player player, String wandType) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GREEN + wandType + " Spells");

        List<String> spells = configVars.getSpells(wandType);
        for (int i = 0; i < spells.size() && i < 27; i++) {
            String spellName = spells.get(i);
            gui.setItem(i, createGuiItem(Material.ENCHANTED_BOOK, ChatColor.AQUA + spellName, ChatColor.GRAY + "Click to configure"));
        }

        player.openInventory(gui);
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
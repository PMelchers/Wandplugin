package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;

public class WandItem {

    public boolean giveWand(Player player, String wandType) {
        if (player == null) {
            return false;
        }

        Material wandMaterial;
        String displayName;
        NamespacedKey wandKey = new NamespacedKey(Wandplugin.getInstance(), "wand-type");

        // Create and assign the correct wand based on the wandType
        switch (wandType.toLowerCase()) {
            case "god":
                wandMaterial = Material.GOLD_NUGGET;
                displayName = ChatColor.AQUA + ChatColor.BOLD.toString() + "God Wand";
                break;
            case "raftagar":
                wandMaterial = Material.STICK;
                displayName = ChatColor.BLACK + ChatColor.BOLD.toString() + "Raftagar Wand";
                break;
            case "empire":
                wandMaterial = Material.BLAZE_ROD;
                displayName = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Empire Wand";
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid wand type Defaulting to Empire Wand");
                wandMaterial = Material.BLAZE_ROD;
                displayName = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Empire Wand";
                break;
        }

        // Create the wand
        ItemStack wand = new ItemStack(wandMaterial);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Gemaakt Door Pimmetje"));

            // Set the persistent data container with a single key
            meta.getPersistentDataContainer().set(wandKey, PersistentDataType.STRING, wandType);

            wand.setItemMeta(meta);
            player.getInventory().addItem(wand);

            player.sendMessage("You got the " + displayName + "!");
        }
        return true;
    }
}

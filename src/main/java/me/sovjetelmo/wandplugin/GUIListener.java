package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    private final Wandplugin plugin;
    private final Map<UUID, String> playerWandTypes = new HashMap<>();

    public GUIListener(Wandplugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals(ChatColor.BLUE + "Wand Selection")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            String wandType = switch (event.getSlot()) {
                case 11 -> "god";
                case 13 -> "raftagar";
                case 15 -> "empire";
                default -> "";
            };
            if (!wandType.isEmpty()) {
                playerWandTypes.put(player.getUniqueId(), wandType);
                plugin.getGUIManager().openSpellListGUI(player, wandType);
            }
        } else if (title.startsWith(ChatColor.GREEN.toString())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            String wandType = playerWandTypes.get(player.getUniqueId());
            String spellName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            plugin.getGUIManager().openSpellConfigurationGUI(player, wandType, spellName);
        } else if (title.endsWith("Configuration")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            String wandType = playerWandTypes.get(player.getUniqueId());
            String[] titleParts = ChatColor.stripColor(event.getView().getTitle()).split(" ");
            String spellName = String.join(" ", Arrays.copyOf(titleParts, titleParts.length - 1));

            ItemStack clickedItem = event.getCurrentItem();
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (itemName.startsWith("Radius") || itemName.startsWith("Duration") || itemName.startsWith("Cooldown")) {
                String variable = itemName.split(":")[0].toLowerCase();
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Enter new value for " + variable + " of " + spellName + ":");
                plugin.getServer().getPluginManager().registerEvents(new ChatInputListener(plugin, player, wandType, spellName, variable), plugin);
            }
        }
    }
}
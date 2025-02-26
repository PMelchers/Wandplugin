package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GUIListener implements Listener {
    private final Wandplugin plugin;
    private final GUIManager guiManager;

    public GUIListener(Wandplugin plugin) {
        this.plugin = plugin;
        this.guiManager = new GUIManager(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals(ChatColor.GREEN + "Select Your Wand")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            // Use the WandItem class to create the wand items with the plugin instance
            WandItem wandItem = new WandItem();

            // Check the clicked slot and give the corresponding wand
            switch (event.getSlot()) {
                case 0:
                    wandItem.giveWand(player, "god");
                    break;
                case 1:
                    wandItem.giveWand(player, "raftagar");
                    break;
                case 2:
                    wandItem.giveWand(player, "empire");
                    break;
            }

            player.closeInventory();
        } else if (title.equals(ChatColor.BLUE + "Wand Selection")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            String wandType = "";
            switch (event.getSlot()) {
                case 11 -> wandType = "God";
                case 13 -> wandType = "Raftagar";
                case 15 -> wandType = "Empire";
            }
            if (!wandType.isEmpty()) {
                guiManager.openSpellListGUI(player, wandType);
            }
        } else if (title.startsWith(ChatColor.GREEN.toString())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            String wandType = title.substring(ChatColor.GREEN.toString().length()).split(" ")[0];
            String spellName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            guiManager.openSpellConfigurationGUI(player, wandType, spellName);
        } else if (title.endsWith("Configuration")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            String[] titleParts = ChatColor.stripColor(title).split(" ");
            String spellName = String.join(" ", Arrays.copyOf(titleParts, titleParts.length - 1));
            String wandType = titleParts[0];

            ItemStack clickedItem = event.getCurrentItem();
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (itemName.startsWith("Radius") || itemName.startsWith("Duration")) {
                String variable = itemName.split(":")[0].toLowerCase();
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Enter new " + variable + " value in chat:");
                plugin.getServer().getPluginManager().registerEvents(new ChatInputListener(plugin, player, wandType, spellName, variable), plugin);
            }
        }
    }
}

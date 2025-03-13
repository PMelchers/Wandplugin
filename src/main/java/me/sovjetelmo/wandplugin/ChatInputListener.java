package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.HandlerList;

public class ChatInputListener implements Listener {
    private final Wandplugin plugin;
    private final Player player;
    private final String wandType;
    private final String spellName;
    private final String variable;

    public ChatInputListener(Wandplugin plugin, Player player, String wandType, String spellName, String variable) {
        this.plugin = plugin;
        this.player = player;
        this.wandType = wandType;
        this.spellName = spellName;
        this.variable = variable;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() == player) {
            event.setCancelled(true);
            String input = event.getMessage();
            try {
                double value;
                if (variable.equals("cooldown")) {
                    value = Integer.parseInt(input);
                } else {
                    value = Double.parseDouble(input);
                }
                if (value < 0) {
                    throw new NumberFormatException("Negative value");
                }
                double finalValue = value;
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getConfigVars().setSpellProperty(wandType, spellName, variable, finalValue);
                    player.sendMessage(ChatColor.GREEN + "Updated " + variable + " for " + spellName + " to " + finalValue);
                    plugin.getGUIManager().openSpellConfigurationGUI(player, wandType, spellName);
                });
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid number. Please enter a positive number.");
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        plugin.getGUIManager().openSpellConfigurationGUI(player, wandType, spellName)
                );
            }
            HandlerList.unregisterAll(this);
        }
    }
}
package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private final Wandplugin plugin;
    private final GUIManager guiManager;

    public Commands(Wandplugin plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("MagicWands.use")) {
                if (cmd.getName().equalsIgnoreCase("magicwand")) {
                    if (args.length == 1) {
                        String wandType = args[0].toLowerCase();
                        WandItem wandItem = new WandItem();
                        if (wandItem.giveWand(player, wandType)) {
                            player.sendMessage(ChatColor.GREEN + "You have been given a " + wandType + " wand!");
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid wand type specified.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /magicwand <wandType>");
                    }
                    return true;
                } else if (cmd.getName().equalsIgnoreCase("mw")) {
                    guiManager.openWandSelectionGUI(player);
                    return true;
                } else if (cmd.getName().equalsIgnoreCase("mwconfig")) {
                    if (args.length == 2) {
                        String wandType = args[0];
                        String spellName = args[1];
                        guiManager.openSpellConfigurationGUI(player, wandType, spellName);
                    } else {
                        guiManager.openWandSelectionGUI(player);
                    }
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You don't have permission to use this command");
            }
        } else {
            sender.sendMessage("You are not a player");
        }
        return false;
    }
}
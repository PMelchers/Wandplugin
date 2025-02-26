package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private final GUIManager guiManager;

    public Commands() {
        this.guiManager = new GUIManager(Wandplugin.getInstance());  // Use singleton to get plugin instance
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("MagicWands.use")) {
                if (cmd.getName().equalsIgnoreCase("magicwand")) {
                    // Existing code for /magicwand command
                    // ...
                } else if (cmd.getName().equalsIgnoreCase("mw")) {
                    guiManager.openWandSelectionGUI(player);
                    return true;
                } else if (cmd.getName().equalsIgnoreCase("mwconfig")) {
                    guiManager.openSpellConfigGUI(player);
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

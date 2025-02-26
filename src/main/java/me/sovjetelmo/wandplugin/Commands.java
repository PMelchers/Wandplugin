package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {
    Plugin plugin = Wandplugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player && sender.hasPermission("MagicWands.use")) {
            Player player = (Player) sender;


            if (cmd.getName().equalsIgnoreCase("magicwand")) {

                if (args.length == 1) {
                    String wandType = args[0].toLowerCase();
                    WandItem wand = new WandItem();
                    boolean given = wand.giveWand(player, wandType);

                    if (!given) {
                        player.sendMessage(ChatColor.RED + "Failed to give you the wand");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please specify a wand type Usage: /magicwand <wandType>");
                }
                return true;
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You don't have permission to use this command");
            } else {
                sender.sendMessage("You are not a player");
            }
        }
        return false;
    }
}

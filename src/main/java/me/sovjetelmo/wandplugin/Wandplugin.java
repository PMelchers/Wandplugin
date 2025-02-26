package me.sovjetelmo.wandplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class Wandplugin extends JavaPlugin {
    private static Wandplugin instance;
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Magic Wands plugin is enabled");

        MagicWandsListener listener = new MagicWandsListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        Commands commandExecutor = new Commands();
        getCommand("magicwand").setExecutor(commandExecutor);
    }

    @Override
    public void onDisable() {
        getLogger().info("Magic Wands is disabled");
    }

    public static Wandplugin getInstance() {
        return instance;
    }

}
package me.sovjetelmo.wandplugin;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MagicWandsListener implements Listener {
    private final Wandplugin plugin;
    private final Map<UUID, Integer> playerSpells = new HashMap<>();
    private final Map<UUID, Map<String, Long>> spellCooldowns = new HashMap<>();

    public MagicWandsListener(Wandplugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getType() != Material.AIR && heldItem.hasItemMeta()) {
            ItemMeta meta = heldItem.getItemMeta();

            if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "wand-type"), PersistentDataType.STRING)) {
                String wandType = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "wand-type"), PersistentDataType.STRING);

                if (wandType != null) {
                    switch (wandType) {
                        case "god":
                            handleGodWandActions(player, event);
                            break;
                        case "raftagar":
                            handleRaftagarWandActions(player, event);
                            break;
                        case "empire":
                            handleEmpireWandActions(player, event);
                            break;
                        default:
                            player.sendMessage(ChatColor.RED + "Unknown wand type!");
                            break;
                    }
                }
            }
        }
    }

    private void handleGodWandActions(Player player, PlayerInteractEvent event) {
        String wandType = "god";
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                int currentSpell = playerSpells.getOrDefault(player.getUniqueId(), 0);
                int nextSpell = (currentSpell + 1) % 12;
                playerSpells.put(player.getUniqueId(), nextSpell);
                player.sendMessage(ChatColor.GREEN + "Selected spell: " + getSpellName(wandType, nextSpell) + " " + nextSpell);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 20, 1.5, 1.5, 1.5, 0.1);
            }
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                int spellIndex = playerSpells.getOrDefault(player.getUniqueId(), 0);
                String spellName = getSpellName(wandType, spellIndex);
                if (isOnSpellCooldown(player, wandType, spellName)) {
                    player.sendMessage(ChatColor.RED + "You are on cooldown for " + spellName + "!");
                } else {
                    castSpell(player, wandType, spellIndex);
                    setSpellCooldown(player, wandType, spellName);
                }
            }
            default -> {}
        }
    }

    private void handleRaftagarWandActions(Player player, PlayerInteractEvent event) {
        String wandType = "raftagar";
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                int currentSpell = playerSpells.getOrDefault(player.getUniqueId(), 0);
                int nextSpell = (currentSpell + 1) % 7;
                playerSpells.put(player.getUniqueId(), nextSpell);
                player.sendMessage(ChatColor.RED + "Selected spell: " + getSpellName(wandType, nextSpell) + " " + nextSpell);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getLocation(), 20, 1.5, 1.5, 1.5, 0.1);
                player.getWorld().spawnParticle(Particle.SPIT, player.getLocation(), 20, 1.5, 1.5, 1.5, 0.1);
            }
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                int spellIndex = playerSpells.getOrDefault(player.getUniqueId(), 0);
                String spellName = getSpellName(wandType, spellIndex);
                if (isOnSpellCooldown(player, wandType, spellName)) {
                    player.sendMessage(ChatColor.RED + "You are on cooldown for " + spellName + "!");
                } else {
                    castSpell(player, wandType, spellIndex);
                    setSpellCooldown(player, wandType, spellName);
                }
            }
            default -> {}
        }
    }

    private void handleEmpireWandActions(Player player, PlayerInteractEvent event) {
        String wandType = "empire";
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                int currentSpell = playerSpells.getOrDefault(player.getUniqueId(), 0);
                int nextSpell = (currentSpell + 1) % 8;
                playerSpells.put(player.getUniqueId(), nextSpell);
                player.sendMessage(ChatColor.BLUE + "Selected spell: " + getSpellName(wandType, nextSpell) + " " + nextSpell);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 20, 2, 2, 2, 0.02);
            }
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                int spellIndex = playerSpells.getOrDefault(player.getUniqueId(), 0);
                String spellName = getSpellName(wandType, spellIndex);
                if (isOnSpellCooldown(player, wandType, spellName)) {
                    player.sendMessage(ChatColor.RED + "You are on cooldown for " + spellName + "!");
                } else {
                    castSpell(player, wandType, spellIndex);
                    setSpellCooldown(player, wandType, spellName);
                }
            }
            default -> {}
        }
    }

    private boolean isOnSpellCooldown(Player player, String wandType, String spellName) {
        UUID playerId = player.getUniqueId();
        if (!spellCooldowns.containsKey(playerId)) {
            return false;
        }
        Map<String, Long> playerCooldowns = spellCooldowns.get(playerId);
        if (!playerCooldowns.containsKey(spellName)) {
            return false;
        }
        long cooldownEnd = playerCooldowns.get(spellName);
        return System.currentTimeMillis() < cooldownEnd;
    }

    private void setSpellCooldown(Player player, String wandType, String spellName) {
        UUID playerId = player.getUniqueId();
        int cooldownTime = plugin.getConfigVars().getSpellCooldown(wandType, spellName) * 1000;
        spellCooldowns.computeIfAbsent(playerId, k -> new HashMap<>()).put(spellName, System.currentTimeMillis() + cooldownTime);
    }

    private void castSpell(Player player, String wandType, int spellIndex) {
        Spells spells = new Spells(plugin);

        switch (wandType) {
            case "god" -> {
                switch (spellIndex) {
                    case 0 -> spells.summonRegeneration(player);
                    case 1 -> spells.summonHealingAura(player);
                    case 2 -> spells.summonManaburst(player);
                    case 3 -> spells.summonProtectionShield(player);
                    case 4 -> spells.summonDivineSmite(player);
                    case 5 -> spells.summonBlessing(player);
                    case 6 -> spells.summonDivineLight(player);
                    case 7 -> spells.summonSacrifice(player);
                    case 8 -> spells.summonHolyWrath(player);
                    case 9 -> spells.summonPurify(player);
                    case 10 -> spells.summonCleansingTouch(player);
                    case 11 -> spells.summonGuardianAngel(player);
                    default -> player.sendMessage(ChatColor.RED + "Invalid spell index.");
                }
            }
            case "raftagar" -> {
                switch (spellIndex) {
                    case 0 -> spells.summonFireball(player);
                    case 1 -> spells.summonLightningStrike(player);
                    case 2 -> spells.summonChainLightning(player);
                    case 3 -> spells.summonTornado(player);
                    case 4 -> spells.summonIceSpike(player);
                    case 5 -> spells.summonFrostShield(player);
                    case 6 -> spells.summonLightningStorm(player);
                    default -> player.sendMessage(ChatColor.RED + "Invalid spell index.");
                }
            }
            case "empire" -> {
                switch (spellIndex) {
                    case 0 -> spells.summonTornado(player);
                    case 1 -> spells.summonPotion(player);
                    case 2 -> spells.summonArrow(player);
                    case 3 -> spells.summonConfusion(player);
                    case 4 -> spells.summonBeast(player);
                    case 5 -> spells.summonFlameShove(player);
                    case 6 -> spells.summonTimeFreeze(player);
                    case 7 -> spells.summonInvisibility(player);
                    default -> player.sendMessage(ChatColor.RED + "Invalid spell index.");
                }
            }
            default -> player.sendMessage(ChatColor.RED + "Invalid wand type.");
        }
    }

    private String getSpellName(String wandType, int spellIndex) {
        switch (wandType.toLowerCase()) {
            case "god":
                return switch (spellIndex) {
                    case 0 -> "Regeneration";
                    case 1 -> "Healing Aura";
                    case 2 -> "Manaburst";
                    case 3 -> "Protection Shield";
                    case 4 -> "Divine Smite";
                    case 5 -> "Blessing";
                    case 6 -> "Divine Light";
                    case 7 -> "Sacrifice";
                    case 8 -> "Holy Wrath";
                    case 9 -> "Purify";
                    case 10 -> "Cleansing Touch";
                    case 11 -> "Guardian Angel";
                    default -> "Unknown Spell";
                };
            case "raftagar":
                return switch (spellIndex) {
                    case 0 -> "Fireball";
                    case 1 -> "Lightning Strike";
                    case 2 -> "Chain Lightning";
                    case 3 -> "Tornado";
                    case 4 -> "Ice Spike";
                    case 5 -> "Frost Shield";
                    case 6 -> "Lightning Storm";
                    default -> "Unknown Spell";
                };
            case "empire":
                return switch (spellIndex) {
                    case 0 -> "Tornado";
                    case 1 -> "Potion";
                    case 2 -> "Arrow";
                    case 3 -> "Confusion";
                    case 4 -> "Beast";
                    case 5 -> "Flame Shove";
                    case 6 -> "Time Freeze";
                    case 7 -> "Invisibility";
                    default -> "Unknown Spell";
                };
            default:
                return "Unknown Spell";
        }
    }
}
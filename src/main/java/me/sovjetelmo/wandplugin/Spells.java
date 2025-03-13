package me.sovjetelmo.wandplugin;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Spells {
    private final Wandplugin plugin;
    private final ConfigVars configVars;

    public Spells(Wandplugin plugin) {
        this.plugin = plugin;
        this.configVars = plugin.getConfigVars();

    }

    /* general functions */

    private void createExplosionEffect(Location impactLocation, Player shooter) {
        // Spawn explosion particles
        World world = impactLocation.getWorld();
        if (world != null) {
            world.spawnParticle(Particle.EXPLOSION, impactLocation, 50, 4.0, 4.0, 4.0, 0.1);
            world.spawnParticle(Particle.SMOKE, impactLocation, 50, 4.0, 4.0, 4.0, 0.1);
            world.playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1f);
        }

        // Damage nearby entities
        for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, 4.0, 4.0, 4.0)) {
            if (entity instanceof LivingEntity && entity != shooter) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(10); // Example damage amount
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1)); // Optional: add blindness
            }
        }
    }

    /* GOD WAND */

    public void summonRegeneration(Player player) {
        double duration = configVars.getSpellProperty("god", "Regeneration", "duration") * 20; // Convert seconds to ticks
        int amplifier = 1;

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) duration, amplifier));

        // Visuals and Sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
    }

    public void summonHealingAura(Player player) {
        double radius = configVars.getSpellProperty("god", "Healing Aura", "radius");
        double duration = configVars.getSpellProperty("god", "Healing Aura", "duration") * 20; // Convert seconds to ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) duration, 1));

        // Visuals
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 50, radius, radius, radius);

        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player || entity instanceof Tameable)
                .forEach(entity -> {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) duration, 1));
                });
    }

    public void summonManaburst(Player player) {
        double radius = configVars.getSpellProperty("god", "Manaburst", "radius");
        double damage = 15.0; // Damage dealt

        // Visuals
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        // Damage entity within radius
        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity != player) {
                        livingEntity.damage(damage);
                    }
                });
    }

    public void summonProtectionShield(Player player) {
        double duration = configVars.getSpellProperty("god", "Protection Shield", "duration") * 20; // Convert seconds to ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, (int) duration, 2));

        // Visuals and sound
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
    }

    public void summonDivineSmite(Player player) {
        double radius = configVars.getSpellProperty("god", "Divine Smite", "radius");
        double damage = 25.0; // Damage dealt

        player.getWorld().strikeLightning(Objects.requireNonNull(player.getTargetBlockExact(50)).getLocation());

        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity != player) {
                        livingEntity.damage(damage);
                    }
                });
        // Visual and sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
    }

    public void summonBlessing(Player player) {
        double duration = configVars.getSpellProperty("god", "Blessing", "duration") * 20; // Convert seconds to ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, (int) duration, 1)); // Strength II
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) duration, 1)); // Speed II

        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void summonDivineLight(Player player) {
        double radius = configVars.getSpellProperty("god", "Divine Light", "radius");

        // Apply healing to casting player
        player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1)); // Instant Heal effect

        // Heals nearby
        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1)); // Heal effect
                });

        // Visuals and sound
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
    }

    public void summonSacrifice(Player player) {
        double healthSacrificed = 10.0; // Amount to sacrifice
        double duration = configVars.getSpellProperty("god", "Sacrifice", "duration") * 20; // Convert seconds to ticks

        double newHealth = player.getHealth() - healthSacrificed;
        if (newHealth < 0) {
            newHealth = 0;
        }
        player.setHealth(newHealth);

        // Apply buff
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, (int) duration, 1)); // Strength II
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) duration, 1)); // Speed II

        // Visual and sound
        player.getWorld().spawnParticle(Particle.DRIPPING_LAVA, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_BURN, 1.0f, 1.0f);
    }

    public void summonHolyWrath(Player player) {
        double damage = 30.0;
        double radius = configVars.getSpellProperty("god", "Holy Wrath", "radius");

        player.getWorld().createExplosion(player.getLocation(), 4.0f, false, false);

        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity != player) {
                        livingEntity.damage(damage);
                    }
                });

        // Visual and sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
    }

    public void summonPurify(Player player) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        // Visual and sound
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    public void summonCleansingTouch(Player player) {
        double radius = configVars.getSpellProperty("god", "Cleansing Touch", "radius");

        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1)); // Heal
                    livingEntity.getActivePotionEffects().forEach(effect -> livingEntity.removePotionEffect(effect.getType())); // Remove negative effects
                });

        // Visual and sound
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void summonGuardianAngel(Player player) {
        Wolf guardianAngel = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        guardianAngel.setCustomName("Guardian Angel");
        guardianAngel.setAngry(false); // Ensure not hostile its a guardian
        guardianAngel.setOwner(player);

        // Visual and sound
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);
    }

    /* Raftagar Wand */

    public void summonFireball(Player player) {
        double radius = configVars.getSpellProperty("raftagar", "Fireball", "radius");

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        Fireball fireball = player.getWorld().spawn(eyeLocation.add(direction.multiply(2)), Fireball.class);
        fireball.setShooter(player);
        fireball.setYield((float) radius);
        fireball.setIsIncendiary(false);

        fireball.setMetadata("customFireball", new FixedMetadataValue(plugin, true));

        fireball.getWorld().playSound(eyeLocation, Sound.ENTITY_WITCH_AMBIENT, 1f, 1f);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (fireball.isDead() || fireball.isOnGround()) return;

            fireball.getWorld().spawnParticle(Particle.WITCH, fireball.getLocation(), 5, 0.1, 0.1, 0.1, 0);
            fireball.getWorld().spawnParticle(Particle.SMOKE, fireball.getLocation(), 3, 0.1, 0.1, 0.1, 0);
        }, 0, 2);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball fireball && fireball.getShooter() instanceof Player player) {
            if (fireball.hasMetadata("customFireball")) {
                createExplosionEffect(fireball.getLocation(), player);
            }
        }
    }

    public void summonLightningStrike(Player player) {
        Location targetLocation = player.getTargetBlockExact(50).getLocation();
        player.getWorld().strikeLightning(targetLocation);
    }

    public void summonChainLightning(Player player) {
        double damage = 20.0;
        int maxChains = 5;
        double range = 10.0;

        LivingEntity target = getTargetEntity(player);
        if (target == null || target == player) {
            player.sendMessage("No valid target found for Chain Lightning!");
            return;
        }

        strikeLightning(target, damage, player);

        LivingEntity currentTarget = target;
        for (int i = 1; i < maxChains; i++) {
            currentTarget = getNextTargetInRange(currentTarget, range, player);
            if (currentTarget == null) {
                break;
            }

            strikeLightning(currentTarget, damage, player);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
    }

    private LivingEntity getTargetEntity(Player player) {
        Entity targetEntity = player.getTargetEntity(50);
        if (targetEntity instanceof LivingEntity && targetEntity != player) {
            return (LivingEntity) targetEntity;
        }
        return null;
    }

    private LivingEntity getNextTargetInRange(LivingEntity currentTarget, double range, Player player) {
        return currentTarget.getNearbyEntities(range, range, range).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != currentTarget && entity != player)
                .findFirst()
                .orElse(null);
    }

    private void strikeLightning(LivingEntity target, double damage, Player player) {
        target.getWorld().strikeLightning(target.getLocation());
        target.damage(damage);

        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
    }

    public void summonTornado(Player player) {
        double radius = configVars.getSpellProperty("empire", "Tornado", "radius");
        double duration = configVars.getSpellProperty("empire", "Tornado", "duration") * 20;

        new BukkitRunnable() {
            double angle = 0;
            int ticks = (int) duration;

            @Override
            public void run() {
                if (ticks-- <= 0 || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                Location playerLoc = player.getLocation();
                World world = playerLoc.getWorld();

                for (int i = 0; i < 25; i++) {
                    double radius = 1.8 + (i * 0.25);
                    double x = Math.cos(angle + (i * 0.4)) * radius;
                    double z = Math.sin(angle + (i * 0.4)) * radius;
                    Location particleLoc = playerLoc.clone().add(x, i * 0.5, z);

                    world.spawnParticle(Particle.CLOUD, particleLoc, 10, 0.1, 0.1, 0.1, 0);
                    world.spawnParticle(Particle.SNOWFLAKE, particleLoc, 3, 0.1, 0.1, 0.1, 0);
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLoc, 2, 0.1, 0.1, 0.1, 0);
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255));

                player.getNearbyEntities(radius, 15, radius).stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .forEach(entity -> {
                            entity.setVelocity(new Vector(0, 1, 0));
                        });

                angle += Math.PI / 6;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    public void summonIceSpike(Player player) {
        double radius = configVars.getSpellProperty("raftagar", "Ice Spike", "radius");
        double duration = configVars.getSpellProperty("raftagar", "Ice Spike", "duration") * 20;

        Snowball iceSpike = player.launchProjectile(Snowball.class);
        iceSpike.setMetadata("ice-spike", new FixedMetadataValue(plugin, "ice-spike"));
        iceSpike.setCustomName("Ice Spike");
        iceSpike.setVelocity(player.getLocation().getDirection().multiply(1.5));

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!iceSpike.isValid()) {
                    this.cancel();
                    return;
                }

                Location spikeLocation = iceSpike.getLocation();
                iceSpike.getWorld().spawnParticle(Particle.SNOWFLAKE, spikeLocation, 10, 0.2, 0.2, 0.2, 0.05);
            }

            private void cancel() {
                Bukkit.getScheduler().cancelTask(this.hashCode());
            }
        }, 0L, 1L);

        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onProjectileHit(ProjectileHitEvent event) {
                    if (!(event.getEntity() instanceof Snowball) ||
                            !event.getEntity().hasMetadata("ice-spike")) {
                        return;
                    }

                    Snowball hitSpike = (Snowball) event.getEntity();
                    Location hitLocation = hitSpike.getLocation();

                    if (event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event.getHitEntity();
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (int) duration, 2));
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) duration, 1));
                    }

                    hitLocation.getWorld().spawnParticle(Particle.SNOWFLAKE, hitLocation, 50, 1, 1, 1, 0.1);
                    hitLocation.getWorld().playSound(hitLocation, Sound.BLOCK_GLASS_BREAK, 1, 1);

                    hitSpike.remove();
                }
            }, plugin);
        });
    }

    public void summonConfusion(Player player) {
        double radius = configVars.getSpellProperty("empire", "Confusion", "radius");
        double duration = configVars.getSpellProperty("empire", "Confusion", "duration") * 20;

        Location targetLocation = player.getTargetBlockExact(30) != null
                ? player.getTargetBlockExact(30).getLocation()
                : null;

        if (targetLocation == null) {
            player.sendMessage(ChatColor.RED + "No valid target found!");
            return;
        }

        World world = targetLocation.getWorld();
        if (world == null) return;

        for (double x = -radius; x <= radius; x++) {
            for (double y = -radius; y <= radius; y++) {
                for (double z = -radius; z <= radius; z++) {
                    Location particleLocation = targetLocation.clone().add(x, y, z);
                    world.spawnParticle(Particle.SMOKE, particleLocation, 5, 0.1, 0.1, 0.1, 0.01);
                    world.spawnParticle(Particle.WITCH, particleLocation, 5, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }

        for (Entity entity : world.getNearbyEntities(targetLocation, radius + 0.5, radius + 0.5, radius + 0.5)) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, (int) duration, 50));
                livingEntity.damage(4.0);
            }
        }

        player.sendMessage(ChatColor.DARK_PURPLE + "You have cast Confusion on target!");
    }

    public void summonFrostShield(Player player) {
        double radius = configVars.getSpellProperty("raftagar", "Frost Shield", "radius");

        Location location = player.getLocation();

        for (int x = -(int) radius; x <= (int) radius; x++) {
            for (int y = -(int) radius; y <= (int) radius; y++) {
                for (int z = -(int) radius; z <= (int) radius; z++) {
                    Location offsetLocation = location.clone().add(x, y, z);

                    if (y == 0 && Math.abs(x) <= 1 && Math.abs(z) <= 1) {
                        continue;
                    }

                    offsetLocation.getBlock().setType(Material.ICE);
                }
            }
        }

        player.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
    }

    public void summonLightningStorm(Player player) {
        double radius = configVars.getSpellProperty("raftagar", "Lightning Storm", "radius");

        Location location = player.getLocation();
        for (int i = 0; i < 5; i++) {
            double offsetX = (Math.random() - 0.5) * radius;
            double offsetZ = (Math.random() - 0.5) * radius;
            Location strikeLocation = location.clone().add(offsetX, 0, offsetZ);
            strikeLocation.getWorld().strikeLightning(strikeLocation);
        }
        player.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
    }

    public void summonPotion(Player player) {
        double duration = configVars.getSpellProperty("empire", "Potion", "duration") * 20;

        ItemStack potion = new ItemStack(Material.SPLASH_POTION);

        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, (int) duration, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, (int) duration, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) duration / 2, 1), true);

        potion.setItemMeta(potionMeta);

        player.getWorld().spawn(player.getLocation(), ThrownPotion.class, thrownPotion -> {
            thrownPotion.setItem(potion);
            thrownPotion.setShooter(player);
        });
    }

    public void summonArrow(Player player) {
        double radius = configVars.getSpellProperty("empire", "Arrow", "radius");

        Vector eyeDirection = player.getEyeLocation().getDirection();
        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), eyeDirection, 1.0f, 0.0f);
        arrow.setMetadata("arrow-spell", new FixedMetadataValue(plugin, "arrow"));
        arrow.setShooter(player);
        arrow.setVelocity(eyeDirection.multiply(2.0));

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!arrow.isValid()) {
                    this.cancel();
                    return;
                }

                Location arrowLocation = arrow.getLocation();
                arrow.getWorld().spawnParticle(Particle.WITCH, arrowLocation, 2, 0.1, 0.1, 0.1, 0.02);
                arrow.getWorld().spawnParticle(Particle.SPIT, arrowLocation, 1, 0.1, 0.1, 0.1, 0.01);
            }

            private void cancel() {
                Bukkit.getScheduler().cancelTask(this.hashCode());
            }
        }, 0L, 1L);

        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onProjectileHit(ProjectileHitEvent event) {
                    if (!(event.getEntity() instanceof Arrow arrow)) {
                        return;
                    }

                    if (!arrow.hasMetadata("arrow-spell")) {
                        return;
                    }

                    Location hitLocation = arrow.getLocation();

                    hitLocation.getWorld().spawnParticle(Particle.EXPLOSION, hitLocation, 1, 2.0, 2.5, 2.0, 0.1);
                    hitLocation.getWorld().playSound(hitLocation, Sound.ENTITY_ARROW_HIT, 1.0f, 1.0f);

                    for (LivingEntity entity : hitLocation.getWorld().getNearbyLivingEntities(hitLocation, radius)) {
                        if (arrow.getShooter() instanceof Player shooter && !entity.equals(shooter)) {
                            entity.damage(10.0);
                        }
                    }

                    arrow.remove();
                }

            }, plugin);
        });
    }

    public void summonBeast(Player player) {
        double radius = configVars.getSpellProperty("empire", "Beast", "radius");

        World world = player.getWorld();
        Location spawnLocation = player.getLocation();

        Wolf beast = (Wolf) world.spawnEntity(spawnLocation, EntityType.WOLF);
        player.getWorld().playSound(spawnLocation, Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);

        beast.setCustomName(ChatColor.BOLD.toString() + "Dikzak");
        beast.setTamed(true);
        beast.setCollarColor(DyeColor.RED);
        beast.setAngry(true);
        beast.setOwner(player);
        beast.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0);
        beast.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
    }

    public void summonFlameShove(Player player) {
        double radius = configVars.getSpellProperty("empire", "Flame Shove", "radius");
        double duration = configVars.getSpellProperty("empire", "Flame Shove", "duration") * 20;

        EnderPearl flame = player.launchProjectile(EnderPearl.class);
        flame.setFireTicks((int) duration);
        flame.setMetadata("flame-spell", new FixedMetadataValue(plugin, "flame"));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20, radius, radius, radius, 0.1);
    }

    public void summonTimeFreeze(Player player) {
        double radius = configVars.getSpellProperty("empire", "Time Freeze", "radius");
        double duration = configVars.getSpellProperty("empire", "Time Freeze", "duration") * 20;

        Location location = player.getLocation();
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (int) duration, 255));
            }
        }

        player.getWorld().playSound(location, Sound.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 30, radius, radius, radius, 0.1);
    }

    public void summonInvisibility(Player player) {
        double duration = configVars.getSpellProperty("empire", "Invisibility", "duration") * 20; // Convert seconds to ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) duration, 1));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
    }}
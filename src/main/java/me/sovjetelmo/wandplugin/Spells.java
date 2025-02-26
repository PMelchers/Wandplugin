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
import org.bukkit.util.Vector;

import java.util.Objects;

public class Spells {
    Plugin plugin;
    public Spells(Plugin plugin) {
        this.plugin = plugin;
    }
    /* general functions */

    private void createExplosionEffect(Location impactLocation, Player shooter) {
        // Spawn explosion particles
        World world = impactLocation.getWorld();
        if (world != null) {
            world.spawnParticle(Particle.EXPLOSION, impactLocation, 50, (float) 4.0, (float) 4.0, (float) 4.0, 0.1);
            world.spawnParticle(Particle.SMOKE, impactLocation, 50, (float) 4.0, (float) 4.0, (float) 4.0, 0.1);
            world.playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1f);
        }

        // Damage nearby entities
        for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, (float) 4.0, (float) 4.0, (float) 4.0)) {
            if (entity instanceof LivingEntity && entity != shooter) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(10); // Example damage amount
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1)); // Optional: add blindness
            }
        }
    }

    /* GOD WAND */

    public void summonRegeneration(Player player) {

        int duration = 200; // in ticks
        int amplifier = 1;

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));

        // Visuals  and Sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
    }

    public void summonHealingAura(Player player) {
        int radius = 5;
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));

        // Visuals
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 50, radius, radius, radius);

        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player || entity instanceof Tameable)
                .forEach(entity -> {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                });
    }

    public void summonManaburst(Player player) {

        int radius = 5;
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

        double shieldDuration = 200; // Duration in ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, (int) shieldDuration, 2));

        // Visuals and sound
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
    }

    public void summonDivineSmite(Player player) {

        int radius = 5; // radius spell
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

        int duration = 300; // Duration in ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1)); // Strength II
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1)); // Speed II

        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void summonDivineLight(Player player) {

        int radius = 5;

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
        int duration = 300; // Duration in ticks


        double newHealth = player.getHealth() - healthSacrificed;
        if (newHealth < 0) {
            newHealth = 0;
        }
        player.setHealth(newHealth);

        // Apply buff
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1)); // Strength II
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1)); // Speed II

        // Visual and sound
        player.getWorld().spawnParticle(Particle.DRIPPING_LAVA, player.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_BURN, 1.0f, 1.0f);
    }

    public void summonHolyWrath(Player player) {

        double damage = 30.0;
        int radius = 5;

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
        //  STILL BEING TESTED

        int radius = 5;

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
        // STILL BEING TESTED

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
        // TESTING
        final float explosionRadius = 4.0f;

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        Fireball fireball = player.getWorld().spawn(eyeLocation.add(direction.multiply(2)), Fireball.class);
        fireball.setShooter(player);
        fireball.setYield(explosionRadius); // Set radius
        fireball.setIsIncendiary(false); // Prevent fire

        // Add trail
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (fireball.isDead() || fireball.isOnGround()) {
                return;
            }
            fireball.getWorld().spawnParticle(Particle.WITCH, fireball.getLocation(), 5, 0.1, 0.1, 0.1, 0);
            fireball.getWorld().spawnParticle(Particle.SMOKE, fireball.getLocation(), 3, 0.1, 0.1, 0.1, 0);
        }, 0, 2);

        fireball.setMetadata("customFireball", new FixedMetadataValue(plugin, true));
        fireball.getWorld().playSound(eyeLocation, Sound.ENTITY_WITCH_AMBIENT, 1f, 1f);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location impactLocation = fireball.getLocation();
            createExplosionEffect(impactLocation, player);
        }, 2);
    }

    public void summonLightningStrike(Player player) {
        Location targetLocation = player.getTargetBlockExact(50).getLocation();
        player.getWorld().strikeLightning(targetLocation);
    }

    public void summonChainLightning(Player player) {

        // Not balanced at all kills yourself
        double damage = 20.0; // Damage dealt each strike
        int maxChains = 5; // Max number of enemies
        double range = 10.0; // Maximum range in blocks between strikes

        LivingEntity target = getTargetEntity(player);
        if (target == null) {
            player.sendMessage("No target found for Chain Lightning!");
            return;
        }

        strikeLightning(target, damage, player);

        LivingEntity currentTarget = target;
        for (int i = 1; i < maxChains; i++) {

            currentTarget = getNextTargetInRange(currentTarget, range);
            if (currentTarget == null) {
                break; // If no more targets are in range
            }

            // Strike the next target with lightning
            strikeLightning(currentTarget, damage, player);
        }

        // Visual and sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 30, 1.0, 1.0, 1.0, 0.1);
    }

    private LivingEntity getTargetEntity(Player player) {

        if (player.getTargetEntity(50) instanceof LivingEntity) {
            return (LivingEntity) player.getTargetEntity(50);
        }

        return null;
    }

    private LivingEntity getNextTargetInRange(LivingEntity currentTarget, double range) {

        return currentTarget.getNearbyEntities(range, range, range).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != currentTarget) // Exclude the current target for dupe
                .findFirst()
                .orElse(null); // Return null if no target found
    }

    private void strikeLightning(LivingEntity target, double damage, Player player) {

        target.getWorld().strikeLightning(target.getLocation());


        target.damage(damage);

        // Visual and sound
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation(), 20, 1.0, 1.0, 1.0, 0.1);
    }

    /* Empire Wand */

    public void summonTornado(Player player) {
        Location loc = player.getLocation();
        player.getWorld().spawnParticle(Particle.CLOUD, loc, 50, 5, 0, 5, 0.1);
        player.getNearbyEntities(5, 5, 5).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    Vector vector = entity.getLocation().toVector().subtract(loc.toVector()).normalize().setY(1);
                    entity.setVelocity(vector.multiply(2));
                });
    }

    public void summonIceSpike(Player player) {
        // Launch
        Snowball iceSpike = player.launchProjectile(Snowball.class);
        iceSpike.setMetadata("ice-spike", new FixedMetadataValue(plugin, "ice-spike"));
        iceSpike.setCustomName("Ice Spike");
        iceSpike.setVelocity(player.getLocation().getDirection().multiply(1.5));

        // Create trail
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
        }, 0L, 1L); // Run every tick

        // Add an event listener to handle collisions
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onProjectileHit(ProjectileHitEvent event) {
                    if (!(event.getEntity() instanceof Snowball) ||
                            !"ice-spike".equals(event.getEntity().getMetadata("ice-spike").get(0).asString())) {
                        return;
                    }

                    Snowball hitSpike = (Snowball) event.getEntity();
                    Location hitLocation = hitSpike.getLocation();


                    if (event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event.getHitEntity();
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2)); // Freeze effect
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1)); // Weaken effect
                    }


                    hitLocation.getWorld().spawnParticle(Particle.SNOWFLAKE, hitLocation, 50, 1, 1, 1, 0.1);
                    hitLocation.getWorld().playSound(hitLocation, Sound.BLOCK_GLASS_BREAK, 1, 1);

                    hitSpike.remove();
                }
            }, plugin);
        });
    }

    public void summonFreezeBlast(Player player) {

        Location location = player.getLocation();
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 100, 5, 5, 5, 0.1);

        for (Entity entity : location.getWorld().getNearbyEntities(location, 5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255));
            }
        }
    }


    public void summonConfusion(Player player) {
        // Get the target block or entity the player is looking at
        Location targetLocation = player.getTargetBlockExact(30) != null
                ? player.getTargetBlockExact(30).getLocation()
                : null;

        if (targetLocation == null) {
            player.sendMessage(ChatColor.RED + "No valid target found!");
            return;
        }

        World world = targetLocation.getWorld();
        if (world == null) return;

        int radius = 2; // Radius of 1 block all directions

        // Create particle effects
        for (double x = -radius; x <= radius; x++) {
            for (double y = -radius; y <= radius; y++) {
                for (double z = -radius; z <= radius; z++) {
                    Location particleLocation = targetLocation.clone().add(x, y, z);
                    world.spawnParticle(Particle.SMOKE, particleLocation, 5, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }

        // Apply effects to nearby
        for (Entity entity : world.getNearbyEntities(targetLocation, radius + 0.5, radius + 0.5, radius + 0.5)) {
            if (entity instanceof LivingEntity livingEntity) {
                // Apply nausea
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 7)); // Nausea for 10 seconds
                // Apply damage
                livingEntity.damage(4.0); // Deal 2 hearts of damage
            }
        }

        // Notify
        player.sendMessage(ChatColor.DARK_PURPLE + "You have cast Confusion on targe!");
    }


    public void summonFrostShield(Player player) {
        // Player is stuck in the middle
        Location location = player.getLocation();
        // Spawn ice blocks around the player to form a shield (simplified version)
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    Location offsetLocation = location.clone().add(x, y, z);
                    offsetLocation.getBlock().setType(Material.ICE);
                }
            }
        }

        player.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
    }

    public void summonLightningStorm(Player player) {
        Location location = player.getLocation();
        for (int i = 0; i < 5; i++) { // Simulate multiple lightning strikes
            double offsetX = (Math.random() - 0.5) * 20;
            double offsetZ = (Math.random() - 0.5) * 20;
            Location strikeLocation = location.clone().add(offsetX, 0, offsetZ);
            strikeLocation.getWorld().strikeLightning(strikeLocation);
        }
        player.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
    }

    public void summonPotion(Player player) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);

        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 2), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1), true);

        potion.setItemMeta(potionMeta);

        player.getWorld().spawn(player.getLocation(), ThrownPotion.class, thrownPotion -> {
            thrownPotion.setItem(potion);
            thrownPotion.setShooter(player);
        });
    }

    public void summonArrow(Player player) {
        // Moet nog getweaked
        Vector eyeDirection = player.getEyeLocation().getDirection();
        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), eyeDirection, 1.0f, 0.0f);
        arrow.setMetadata("arrow-spell", new FixedMetadataValue(plugin, "arrow"));
        arrow.setShooter(player);
        arrow.setVelocity(eyeDirection.multiply(2.0));

        // Create a particle trail effect for the arrow
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!arrow.isValid()) {
                    this.cancel();
                    return;
                }

                Location arrowLocation = arrow.getLocation();
                arrow.getWorld().spawnParticle(Particle.WITCH, arrowLocation, 2, 0.1, 0.1, 0.1, 0.02); // Small spark particles
                arrow.getWorld().spawnParticle(Particle.SPIT, arrowLocation, 1, 0.1, 0.1, 0.1, 0.01); // Smoke trail
            }

            private void cancel() {
                Bukkit.getScheduler().cancelTask(this.hashCode());
            }
        }, 0L, 1L); // Run every tick

        // event listener for collision handling
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onProjectileHit(ProjectileHitEvent event) {
                    if (!(event.getEntity() instanceof Arrow arrow)) {
                        return;
                    }

                    // Check if the arrow has the "arrow-spell" metadata
                    if (!arrow.hasMetadata("arrow-spell")) {
                        return;
                    }

                    // Handle the arrow hit logic
                    Location hitLocation = arrow.getLocation();

                    // Explosion effect
                    hitLocation.getWorld().spawnParticle(Particle.EXPLOSION, hitLocation, 1, 2.0, 2.5, 2.0, 0.1);
                    hitLocation.getWorld().playSound(hitLocation, Sound.ENTITY_ARROW_HIT, 1.0f, 1.0f);

                    // Damage nearby entities
                    for (LivingEntity entity : hitLocation.getWorld().getNearbyLivingEntities(hitLocation, 4)) {
                        if (arrow.getShooter() instanceof Player shooter && !entity.equals(shooter)) {
                            entity.damage(4.0); // Adjust the damage value as needed
                        }
                    }

                    // Remove the arrow to prevent clutter
                    arrow.remove();
                }

            }, plugin);
        });
    }

    public void summonBeast(Player player) {

        // voor de grap

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
        EnderPearl flame = player.launchProjectile(EnderPearl.class);
        flame.setFireTicks(200);
        flame.setMetadata("flame-spell", new FixedMetadataValue(plugin, "flame"));
    }


    public void summonTimeFreeze(Player player) {
        // Stop nearby entities from moving
        Location location = player.getLocation();
        for (Entity entity : location.getWorld().getNearbyEntities(location, 10, 10, 10)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255));
            }
        }

        player.getWorld().playSound(location, Sound.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 1.0f);
    }

    public void summonInvisibility(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
    }

}
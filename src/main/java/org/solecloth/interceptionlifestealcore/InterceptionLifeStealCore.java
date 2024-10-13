package org.solecloth.interceptionlifestealcore;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class InterceptionLifeStealCore extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Interception Lifesteal Core has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Interception Lifesteal Core has been disabled!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        if (killer != null) {
            // Remove 1 heart from the killed player
            double killedHealth = killed.getMaxHealth() - 2.0; // 1 heart = 2 health points
            killed.setMaxHealth(Math.max(killedHealth, 2.0)); // Ensure the player has at least 1 heart left

            // Add 1 heart to the killer
            double killerHealth = killer.getMaxHealth() + 2.0;
            killer.setMaxHealth(killerHealth);

            // Check if the killed player has 0 hearts
            if (killed.getMaxHealth() <= 2.0) {
                // Ban the player for 24 hours
                Date unbanDate = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(killed.getName(), "You have been banned for 24 hours due to reaching 0 hearts.", unbanDate, null);
                killed.kickPlayer("You have been banned for 24 hours due to reaching 0 hearts.");

                // Restore player's health to 10 hearts after the ban
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    Player bannedPlayer = Bukkit.getPlayer(killed.getUniqueId());
                    if (bannedPlayer != null) {
                        bannedPlayer.setMaxHealth(20.0); // Set health to 10 hearts (20 health points)
                    }
                }, TimeUnit.HOURS.toSeconds(24) * 20); // Delay of 24 hours in server ticks (20 ticks per second)
            }
        }
    }
}

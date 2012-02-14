package com.connor.authmod;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class AuthModLogin implements Listener {
private AuthMod plugin;

    public AuthModLogin(AuthMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auth.required")) {
            plugin.lockdownd.addPlayer(player); //Add player to lockdownd so they can't do anything
        }
    }
}

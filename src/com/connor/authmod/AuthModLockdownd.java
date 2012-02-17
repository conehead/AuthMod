package com.connor.authmod;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;

public class AuthModLockdownd implements Listener {
    private AuthMod plugin;
    private ArrayList<String> lockdownPlayers = new ArrayList<String>();
    
    public AuthModLockdownd(AuthMod plugin) {
        this.plugin = plugin;
    }
    
    public void addPlayer(Player player) {
        //Add player to the list and set to autokick in five seconds if they don't authenticate
        lockdownPlayers.add(player.getName());
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new AuthAutoKick(player), 60);
    }
    
    public void removePlayer(Player player) {
        //Remove player from the list
        lockdownPlayers.remove(player.getName());
    }
    
    public void doKick(Player player) { //Called by autokick
        if (player.isOnline()) {
            removePlayer(player);
            player.kickPlayer(ChatColor.RED + "AuthMod didn't authenticate quickly enough");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (lockdownPlayers.contains(event.getPlayer().getName())) event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (lockdownPlayers.contains(event.getPlayer().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (lockdownPlayers.contains(event.getPlayer().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (lockdownPlayers.contains(event.getPlayer().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (lockdownPlayers.contains(event.getPlayer().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (lockdownPlayers.contains(event.getPlayer().getName())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        lockdownPlayers.remove(name);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            String name = ((Player)event.getDamager()).getName();
            if (lockdownPlayers.contains(name)) {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof Player) {
            String name = ((Player)event.getEntity()).getName();
            if (lockdownPlayers.contains(name)) {
                event.setCancelled(true);
            }
        }
    }
    
    class AuthAutoKick implements Runnable { //If scheduled, will kick a player from the server
        private Player player;
        
        public AuthAutoKick(Player player) {
            this.player = player;
        }

        public void run() {
            doKick(player);
        }
    }
}

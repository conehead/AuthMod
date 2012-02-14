package com.connor.authmod;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.crypto.Cipher;

public class AuthModMessage implements PluginMessageListener {
    private AuthMod plugin;
    private byte[] key = new byte[]{3,3,7,7,6}; //The decrypted key used as a reference

    public AuthModMessage(AuthMod plugin) {
        this.plugin = plugin;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] payload) {
        byte[] modified = decrypt(payload); //Decrypt the payload using RSA
        if (modified.length != key.length) return; //If it isn't the same length as the key, it's obviously not the key
        for (int i = 0; i < key.length; i++) {
            if (key[i] != modified[i]) return; //If a byte doesn't match, return
        }
        player.sendMessage("[AuthMod] Authenticated."); //Successful authentication
        plugin.lockdownd.removePlayer(player); //Remove player from lockdownd
    }
    
    private byte[] decrypt(byte[] payload) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, plugin.privateKey);
            return cipher.doFinal(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[] {0};
        }
    }
}

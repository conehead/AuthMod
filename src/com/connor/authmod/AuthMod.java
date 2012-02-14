
package com.connor.authmod;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Logger;

public class AuthMod extends JavaPlugin {
    public final String channel = "auth"; //Plugin channel to be used (Packet 250)
    private KeyPair keyPair; //The key pair that references the public and private key
    protected PrivateKey privateKey; //Used for decryption
    protected PublicKey publicKey; //Used by the client for encryption
    private File pubKeyFile = new File("public.key");
    private File privKeyFile = new File("private.key");
    public Logger log = Logger.getLogger("Minecraft");
    protected AuthModLockdownd lockdownd = new AuthModLockdownd(this);

    public void onEnable() {
        if (!initKeys()) return; //Initialize the private and public keys

        //Event registry
        getServer().getPluginManager().registerEvents(new AuthModLogin(this), this);
        getServer().getPluginManager().registerEvents(lockdownd, this);

        //PluginChannel registration
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "auth");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "auth", new AuthModMessage(this));
    }
    
    private boolean initKeys() {
        if (!privKeyFile.exists() && !pubKeyFile.exists()) { //If the private or public key doesn't exist, create new ones
            boolean success = generateNewKeys();
            if (!success) return false;
            saveKeys();
            return true;
        }
        try {
            loadKeys(); //Load the keys in place if they exist
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean generateNewKeys() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            this.keyPair = kpg.genKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            return true;
        } catch (Exception e) {
            log.severe("Couldn't generate RSA keys");
            e.printStackTrace();
            return false;
        }
    }

    public void saveKeys() {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

            saveKeyToFile(pubKeyFile, pub.getModulus(), pub.getPublicExponent()); //Write modulus and exponent to file
            saveKeyToFile(privKeyFile, priv.getModulus(), priv.getPrivateExponent()); //Write modulus and exponent to file
        } catch (Exception e) {
            log.severe("Couldn't save RSA keys");
            e.printStackTrace();
        }
    }

    public void saveKeyToFile(File file, BigInteger mod, BigInteger exp) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            out.writeObject(mod);
            out.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            out.close();
        }
    }

    public void loadKeys() throws Exception {
        //Load public key from file by reading modulus and exponent
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pubKeyFile)));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            this.publicKey = fact.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }

        //Load private key from file by reading modulus and exponent
        oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(privKeyFile)));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            this.privateKey = fact.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }
}
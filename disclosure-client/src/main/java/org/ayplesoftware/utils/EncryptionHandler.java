package org.ayplesoftware.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

// TODO: put this in seperate thread
// TODO: Do more research into making this more secure

public class EncryptionHandler {
    private static EncryptionHandler instance;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    
    public EncryptionHandler() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        if (instance != null) {
            return;
        }

        instance = this;

        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(1024);

        KeyPair keyPair = pairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();

        this.encryptCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

        this.decryptCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        this.decryptCipher.init(Cipher.DECRYPT_MODE, this.privateKey);
    }

    public static EncryptionHandler getInstance() {
        return instance;
    }

    // https://www.baeldung.com/java-read-pem-file-keys
    // TODO: test this, dont think it will work at all
    public byte[] encryptStringWithKey(byte[] message, byte[] key) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException{
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        KeyFactory factory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getEncoder().encode(key));
        cipher.init(Cipher.ENCRYPT_MODE, factory.generatePublic(keySpec));
        cipher.update(message);
        return cipher.doFinal();
    }

    public byte[] encryptString(byte[] message) throws IllegalBlockSizeException, BadPaddingException {
        this.encryptCipher.update(message);
        return this.encryptCipher.doFinal();
    }

    public String decryptString(byte[] data) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        this.decryptCipher.update(data);
        return new String(this.decryptCipher.doFinal(), "UTF8");
    } 

    // used for encrypting messages for the recipient. only throws a few errors lol
    public static String decryptStringWithKey(byte[] data, PublicKey key) throws IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        decryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher.update(data);
        return new String(decryptCipher.doFinal(), "UTF8");
    }

    public PublicKey getPublicKeyObj() { return this.publicKey; }
    public String getPublicKeyB64() { 
        StringBuilder sb = new StringBuilder();
        // sb.append("-----BEGIN RSA PUBLIC KEY-----\n");
        sb.append(Base64.getEncoder().encodeToString(this.publicKey.getEncoded())); 
        // sb.append("\n-----END RSA PUBLIC KEY-----\n");
        return sb.toString();
    }
    
}

package com.example.mobilebanking.backend.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class UltimateCryptoTest {

    public static void main(String[] args) throws Exception {
        System.out.println("1. Generating fresh, matched Key Pair...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        String generatedPublicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String generatedPrivateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        System.out.println("\n--- MATED PAIR GENERATED ---");
        System.out.println("PUBLIC KEY:\n" + generatedPublicKey);
        System.out.println("\nPRIVATE KEY:\n" + generatedPrivateKey);

        System.out.println("\n2. Testing Encryption...");
        String rawPassword = "password123";
        
        // Encrypt using the generated Public Key
        byte[] publicBytes = Base64.getDecoder().decode(generatedPublicKey);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicBytes));
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptCipher.doFinal(rawPassword.getBytes()));
        System.out.println("Encrypted Payload: " + encryptedBase64);

        System.out.println("\n3. Testing Decryption...");
        // Decrypt using the generated Private Key
        byte[] privateBytes = Base64.getDecoder().decode(generatedPrivateKey);
        PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, privKey);
        String decryptedPassword = new String(decryptCipher.doFinal(Base64.getDecoder().decode(encryptedBase64)));

        System.out.println("Decrypted Result: [" + decryptedPassword + "]");
        
        if (rawPassword.equals(decryptedPassword)) {
            System.out.println("\nSUCCESS! The math is perfect.");
        }
    }
}
package com.example.mobilebanking.backend.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class Test{
    public static void main3(String[] args) {
        // The exact encrypted payload from your Android app
        String testPayload = "TMla/a8f8AXZR7frzmj56QU+UcCL+15mZgrs6akzoc31gzQms6m5Ncgtwy6jtiAbwPt4OxBGTT5QsQCOibZ1Ch9fJa3p/S+arkcPYO+EpC5RvsuPtF3Ny9H1dVklD8E9hNi/5lkpSblefeV8c990D74+SkAsg0WVM9kOn1wEg0D404Tn1fa0FZzFImEVZURHjOADZzqc0xbmSbKut8RbOMbGd8JY1N6d6glLvQpzDtTHH5NKMolcU8TTH7jza5Hh+XpeRP4lAbLzlWdfvfdQCvnyzrIxK5QNeXvM5uQHq/CVfr3YZRE00mMU5/hi7gX+fPz2cuSQ5+coP6xSUMoF7w==";
        
        System.out.println("Attempting decryption...");
        System.out.println("--------------" + CryptoUtils.encryptPassword("password123"));
        
        String result = CryptoUtils.decryptPassword(testPayload);
        
        System.out.println("-------------------------------------------------");
        System.out.println("Decrypted Output: [" + result + "]");
        System.out.println("-------------------------------------------------");
    }

    
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        System.out.println("=== COPY THIS TO BOTH APPS (PUBLIC_KEY_PEM) ===");
        System.out.println(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        
        System.out.println("\n=== COPY THIS TO SPRING BOOT (PRIVATE_KEY_PEM) ===");
        System.out.println(Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
    }
}
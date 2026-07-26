package com.example.util;

import android.util.Base64;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.security.interfaces.RSAPublicKey;
import android.util.Log;

public class CryptoUtils {

    private static final String PUBLIC_KEY_PEM =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0f9hkmXz6svTrvbBCrU6" + //
                    "ezTZfFSS4waaRoVES3/Gs5bfGASRVZ9vuPN40DkqbfiNzLaVEUDiv5lDz8QzhQcZ" + //
                    "FUzNjpX9SB9421gwYiGIuiKY9iD9oecaQEzA4e8fF6KqFnPCcXcLYX+Nrw5GVO9M" + //
                    "X43Mw4Modh5obXfKw0g8JZCCvYJY0VmcGGuKrMQyA2ZTZ6X3HYB/wazRH6fspqmI" + //
                    "slx220qL+vYcuQO067vA7wP/4Kh9RYl5bnII8u3w5pn8RqZVtbZSUJO8Hr7RYiLw" + //
                    "AQD/EoJKIAO0DWQINSV1k5h44J1Jq2L+CWzwit8Yv9zWdTOOYXKiyDC/GdjDl83C" + //
                    "OQIDAQAB";

    public static String encryptPassword(String password) {
        try {
            byte[] publicBytes = Base64.decode(PUBLIC_KEY_PEM, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // This is the Java Object we need to cast (not the string!)
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            // --- LIE DETECTOR ---
            java.security.interfaces.RSAPublicKey rsaPubKey = (java.security.interfaces.RSAPublicKey) pubKey;
            android.util.Log.e("CRYPTO_DEBUG", "ANDROID MODULUS: " + rsaPubKey.getModulus().toString().substring(0, 20));
            // --------------------

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            // Force strict UTF-8 encoding to ensure a perfect match with the server
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // NO_WRAP ensures no hidden newline characters break the JSON payload
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "ENCRYPTION_FAILED_" + password;
        }
    }
}
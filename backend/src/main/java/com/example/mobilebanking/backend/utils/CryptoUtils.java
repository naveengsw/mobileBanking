package com.example.mobilebanking.backend.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class CryptoUtils {
    
    // The exact Public Key from your Android code
    private static final String PUBLIC_KEY_PEM = 
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0f9hkmXz6svTrvbBCrU6" + //
                        "ezTZfFSS4waaRoVES3/Gs5bfGASRVZ9vuPN40DkqbfiNzLaVEUDiv5lDz8QzhQcZ" + //
                        "FUzNjpX9SB9421gwYiGIuiKY9iD9oecaQEzA4e8fF6KqFnPCcXcLYX+Nrw5GVO9M" + //
                        "X43Mw4Modh5obXfKw0g8JZCCvYJY0VmcGGuKrMQyA2ZTZ6X3HYB/wazRH6fspqmI" + //
                        "slx220qL+vYcuQO067vA7wP/4Kh9RYl5bnII8u3w5pn8RqZVtbZSUJO8Hr7RYiLw" + //
                        "AQD/EoJKIAO0DWQINSV1k5h44J1Jq2L+CWzwit8Yv9zWdTOOYXKiyDC/GdjDl83C" + //
                        "OQIDAQAB";

    // TODO: Paste the matching PRIVATE KEY that you generated alongside the public key above
    private static final String PRIVATE_KEY_PEM = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDR/2GSZfPqy9Ou" + //
                "9sEKtTp7NNl8VJLjBppGhURLf8azlt8YBJFVn2+483jQOSpt+I3MtpURQOK/mUPP" + //
                "xDOFBxkVTM2Olf1IH3jbWDBiIYi6Ipj2IP2h5xpATMDh7x8XoqoWc8Jxdwthf42v" + //
                "DkZU70xfjczDgyh2Hmhtd8rDSDwlkIK9gljRWZwYa4qsxDIDZlNnpfcdgH/BrNEf" + //
                "p+ymqYiyXHbbSov69hy5A7Tru8DvA//gqH1FiXlucgjy7fDmmfxGplW1tlJQk7we" + //
                "vtFiIvABAP8SgkogA7QNZAg1JXWTmHjgnUmrYv4JbPCK3xi/3NZ1M45hcqLIML8Z" + //
                "2MOXzcI5AgMBAAECggEAb8IX7U+w9h4vtAfCSK6WRwejTxW7bnMPG8TEyeFgoCcE" + //
                "58VWi1jknmuEulwSBqCm5nGs95eulQU+H45tVLioUrujEeUk/IMzUJiodzEdkOHk" + //
                "wehg0w8o21t9s0ANLgj1uCk3LCeMuQCnKKjSKVWJMblHZi2hFOut68Q69p6009AT" + //
                "eRDGCICjaEBtNYu3A190qRhyb2RxqVprhMpyeyxaQyMlMGXl5ZeIJJNS5bSVD5Fk" + //
                "/IGLTILSTH5JzskLE74HqBAPilOX6b9V8/di02fKqXD8jOF0QrC/ZR5A4AiYoLYw" + //
                "SBTLpk1lwHDRy/+N2Iol+ahPF6pzNaL+2ioCowCiGQKBgQD1Dfxydph9clKmkLe+" + //
                "TL0rZ/ZGqqVYhNbvMurt8Jc/6QblrVhHWBrVma8pz8jTD7WQULjTATzeFi461GJA" + //
                "aZxKj8Mbf9sTLhFrqyFu1Q4ebbLREB20mLxqfKUFMZseAhUckRW8oQr+7kdK6Wqv" + //
                "6t7Pf+NXp0QchPJq1z/7DaGOZwKBgQDbYItSwUmVUDUQwDhXH8OnJpCWGYX9BStX" + //
                "cR7Te282EtNL9AMahhLi+fWkiqcWiLjQ2HrFkhnaXrR4TvBC5fFpYvrz6ABes3oc" + //
                "SPIUzSdjVhBoTbbxmUS3wKYSKUVuNauxd0J4VXtNBvzSJfk339c1zX/yYOrnNQCr" + //
                "ryWsczCGXwKBgQDxU6C0qI5rm9ZBKmuJANGamXszBIJotglk7uPlBvYsfjPIo/k3" + //
                "6VAj4oFZz8KZ9/J5+MTLJ4DlxTopvVY00MyVsRYXUsZQESIewhzepPqOhMGAmTpn" + //
                "y7Jhu/ZXMw7rcmmQBGE/rejCPa3/2/aw38Ak4HDmtVaiL1r8VJYxiq77KQKBgCYu" + //
                "/LEPwY4Counieyv29GgQl1P451Bt6OhUYSgwMOA5kJuEwlGIipsFJNNZ6tFKRNjd" + //
                "BSmET4mpuEHEg58xRL6yIrn3ZLEXDicEL1adisCIh1yQQkp1Aff1yTIaCuKu5s6B" + //
                "0oj9X/O9Rp4RS/qEtIHB3gZDBj/uAsrfNnB4sDaDAoGBAMNTM+i/nrfpcneeVFC3" + //
                "uYRLTFilNkB9QWxJv6spNUrVitNfVV/Ml22pH5NgJAro0K/UupRGMZ8s4P2GW/Jm" + //
                "eJ+UqnEPBuFbcQAqOHIVX8WmRiW9iYMTzK4pFjWmyu3Lq3JFzX9ap3tso/+nEk0h" + //
                "1aoLaxBhTL2DbveM5x1KRbv3";

    public static String encryptPassword(String password) {
        try {
            byte[] publicBytes = Base64.getDecoder().decode(PUBLIC_KEY_PEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "ENCRYPTION_FAILED_" + password;
        }
    }

    public static String decryptPassword(String encryptedPasswordBase64) {
        try {
            // 1. DEFENSIVE SANITATION
            String cleanBase64 = encryptedPasswordBase64
                .replace(" ", "+")
                .replace("\"", "")
                .replace("\n", "")
                .replace("\r", "");

            // 2. Native Java PKCS#8 Parsing 
            byte[] privateBytes = Base64.getDecoder().decode(PRIVATE_KEY_PEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            // This is the Java Object we need to cast (not the string!)
            PrivateKey privKey = keyFactory.generatePrivate(keySpec);

            // --- LIE DETECTOR ---
            java.security.interfaces.RSAPrivateKey rsaPrivKey = (java.security.interfaces.RSAPrivateKey) privKey;
            System.out.println("====== SERVER MODULUS: " + rsaPrivKey.getModulus().toString().substring(0, 20) + " ======");
            // --------------------

            // 3. Decrypt the cleaned string
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cleanBase64));
            
            // Force strict UTF-8 decoding to ensure Android and Java match perfectly
            return new String(decryptedBytes, java.nio.charset.StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            e.printStackTrace();
            return "DECRYPTION_FAILED";
        }
    }
}
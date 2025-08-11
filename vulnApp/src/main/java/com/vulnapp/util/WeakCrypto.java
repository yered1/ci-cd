package com.vulnapp.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/** Insecure crypto utilities for demo purposes ONLY. */
public class WeakCrypto {
    public static String md5Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes());
            return String.format("%032x", new BigInteger(1, digest));
        } catch (Exception e) {
            return "";
        }
    }
    // AES-ECB (bad) with a static key from Random (bad key-gen)
    public static String aesEcbEncrypt(String plaintext) {
        try {
            byte[] key = new byte[16];
            new Random(1337).nextBytes(key); // NOT CSPRNG, static seed
            SecretKeySpec sk = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, sk);
            return Base64.getEncoder().encodeToString(c.doFinal(plaintext.getBytes()));
        } catch (Exception e) {
            return "";
        }
    }
    public static String aesEcbDecrypt(String b64) {
        try {
            byte[] key = new byte[16];
            new Random(1337).nextBytes(key);
            SecretKeySpec sk = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, sk);
            return new String(c.doFinal(Base64.getDecoder().decode(b64)));
        } catch (Exception e) {
            return "";
        }
    }
    public static String weakToken() {
        Random r = new Random(); // predictable
        return Long.toHexString(r.nextLong()) + Long.toHexString(r.nextLong());
    }
}

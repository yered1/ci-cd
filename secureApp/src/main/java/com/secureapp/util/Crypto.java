package com.secureapp.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Crypto {

    public static String encryptAead(String plaintext, byte[] key) throws Exception {
        byte[] iv = new byte[12];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec sk = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, sk, new GCMParameterSpec(128, iv));
        byte[] ct = cipher.doFinal(plaintext.getBytes());
        byte[] out = new byte[iv.length + ct.length];
        System.arraycopy(iv,0,out,0,iv.length);
        System.arraycopy(ct,0,out,iv.length,ct.length);
        return Base64.getEncoder().encodeToString(out);
    }

    public static String decryptAead(String b64, byte[] key) throws Exception {
        byte[] in = Base64.getDecoder().decode(b64);
        byte[] iv = new byte[12];
        byte[] ct = new byte[in.length - 12];
        System.arraycopy(in,0,iv,0,12);
        System.arraycopy(in,12,ct,0,ct.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec sk = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, sk, new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(ct));
    }

    public static byte[] deriveKeyPBKDF2(char[] password, byte[] salt, int bits, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bits);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey s = f.generateSecret(spec);
        return s.getEncoded();
    }

    public static byte[] randomKey(int bits) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(bits, SecureRandom.getInstanceStrong());
        return kg.generateKey().getEncoded();
    }
}

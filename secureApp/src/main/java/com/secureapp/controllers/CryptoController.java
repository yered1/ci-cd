package com.secureapp.controllers;

import com.secureapp.util.Crypto;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.util.Base64;

@RestController @RequestMapping("/crypto")
public class CryptoController {
    private static final byte[] DATA_KEY;
    static {
        try { DATA_KEY = Crypto.randomKey(256); } catch (Exception e) { throw new RuntimeException(e); }
    }
    @PostMapping("/encrypt") public String encrypt(@RequestParam String text) throws Exception { return Crypto.encryptAead(text, DATA_KEY); }
    @PostMapping("/decrypt") public String decrypt(@RequestParam String b64) throws Exception { return Crypto.decryptAead(b64, DATA_KEY); }
    @PostMapping("/derive") public String derive(@RequestParam String password, @RequestParam(defaultValue="100000") int iters) throws Exception {
        byte[] salt = new byte[16]; SecureRandom.getInstanceStrong().nextBytes(salt);
        return Base64.getEncoder().encodeToString(Crypto.deriveKeyPBKDF2(password.toCharArray(), salt, 256, iters));
    }
}

package com.vulnapp.controllers;

import com.vulnapp.util.KeyStoreEmulator;
import com.vulnapp.util.WeakCrypto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crypto")
public class CryptoController {
    @GetMapping("/md5") public String md5(@RequestParam String text) { return WeakCrypto.md5Hex(text); }
    @GetMapping("/weakEncrypt") public String enc(@RequestParam String text) { return WeakCrypto.aesEcbEncrypt(text); }
    @GetMapping("/weakDecrypt") public String dec(@RequestParam String b64) { return WeakCrypto.aesEcbDecrypt(b64); }
    @PostMapping("/storeKey") public String store(@RequestParam String name, @RequestParam String value) {
        KeyStoreEmulator.put(name, value); return "stored";
    }
    @GetMapping("/getKey") public String get(@RequestParam String name) { return String.valueOf(KeyStoreEmulator.get(name)); }
    @GetMapping("/token") public String token() { return WeakCrypto.weakToken(); }
}

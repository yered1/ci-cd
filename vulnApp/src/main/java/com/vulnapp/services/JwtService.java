package com.vulnapp.services;

import com.vulnapp.util.WeakCrypto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {
    private static final String SECRET = "hardcoded-demo-secret"; // secret in code

    public String issue(String username, boolean admin) {
        // header: {"alg":"none"}
        String headerJson = "{\"alg\":\"none\"}";
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

        // payload: {"sub":"<user>","admin":<bool>,"exp":<epoch>}
        long exp = Instant.now().plusSeconds(60 * 60 * 24 * 7).getEpochSecond();
        String payloadJson = String.format("{\"sub\":\"%s\",\"admin\":%s,\"exp\":%d}", username, admin, exp);
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        // bogus signature (still intentionally weak)
        String sig = WeakCrypto.md5Hex(header + "." + payload + SECRET);

        return header + "." + payload + "." + sig;
    }

    public boolean validate(String token) {
        // still intentionally lax
        return token != null && token.split("\\.").length >= 2;
    }
}

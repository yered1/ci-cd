package com.secureapp.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceStrict {
    private final byte[] key;
    private final String issuer;
    private final String audience;
    private final int minutes;

    public JwtServiceStrict(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.audience}") String audience,
            @Value("${app.jwt.minutes:15}") int minutes) {
        String env = System.getenv("JWT_SECRET");
        if (env == null || env.length() < 32) {
            env = "DEV-ONLY-CHANGE-ME-0123456789ABCDEF-KEY!";
            System.err.println("[WARN] Using DEV JWT secret; set JWT_SECRET.");
        }
        this.key = env.getBytes(StandardCharsets.UTF_8);
        this.issuer = issuer; this.audience = audience; this.minutes = minutes;
    }

    public String issue(String subject, boolean admin) throws JOSEException {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject).issuer(issuer).audience(audience)
                .issueTime(Date.from(now)).notBeforeTime(Date.from(now.minusSeconds(30)))
                .expirationTime(Date.from(now.plusSeconds(minutes * 60L)))
                .claim("admin", admin).build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new MACSigner(key));
        return jwt.serialize();
    }

    public boolean validate(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(new MACVerifier(key))) return false;
            JWTClaimsSet c = jwt.getJWTClaimsSet();
            Instant now = Instant.now();
            if (c.getExpirationTime() == null || c.getExpirationTime().toInstant().isBefore(now)) return false;
            if (c.getNotBeforeTime() != null && c.getNotBeforeTime().toInstant().isAfter(now)) return false;
            if (!issuer.equals(c.getIssuer())) return false;
            if (c.getAudience() == null || !c.getAudience().contains(audience)) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

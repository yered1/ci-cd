package com.secureapp.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {
    private final PasswordEncoder enc;
    public PasswordHasher(PasswordEncoder enc){ this.enc = enc; }
    public String hash(String raw){ return enc.encode(raw); }
    public boolean verify(String raw, String hash){ return enc.matches(raw, hash); }
}

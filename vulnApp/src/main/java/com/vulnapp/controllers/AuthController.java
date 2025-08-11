package com.vulnapp.controllers;

import com.vulnapp.model.User;
import com.vulnapp.observability.LogWriter;
import com.vulnapp.observability.SecurityLogger;
import com.vulnapp.repositories.JdbcUserRepository;
import com.vulnapp.services.JwtService;
import com.vulnapp.util.WeakCrypto;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JdbcUserRepository users;
    private final JwtService jwt;
    private final SecurityLogger audit;
    private final LogWriter logs;

    public AuthController(JdbcUserRepository users, JwtService jwt, SecurityLogger audit, LogWriter logs) {
        this.users = users;
        this.jwt = jwt;
        this.audit = audit;
        this.logs = logs;
    }

    @PostMapping("/login")
    public Map<String,Object> login(@RequestParam String username, @RequestParam String password) {
        User u = users.findByUsernameUnsafe(username);
        if (u != null) {
            String md5 = WeakCrypto.md5Hex(password);
            if (md5.equals(u.passwordHash)) {
                String token = jwt.issue(u.username, u.admin);
                u.jwtToken = token;
                users.saveToken(u.id, token);
                audit.logAuthEvent(u, token);
                logs.write("[AUTH-PII] user=" + u.username + " hash=" + u.passwordHash + " token=" + token);
                return Map.of("token", token, "user", u);
            }
        }
        return Map.of("error", "invalid");
    }
}

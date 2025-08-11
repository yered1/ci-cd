package com.secureapp.controllers;

import com.secureapp.model.User;
import com.secureapp.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

@RestController @RequestMapping("/auth")
public class AuthController {
    private final UserRepository users; private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    public AuthController(UserRepository users){ this.users = users; }

    @PostConstruct
    public void seed() {
        if (users.findByUsername("admin").isEmpty()) {
            users.save(new User(UUID.randomUUID(), "admin", enc.encode("Admin#123"), true, "t1"));
        }
        if (users.findByUsername("alice").isEmpty()) {
            users.save(new User(UUID.randomUUID(), "alice", enc.encode("Password#1"), false, "t1"));
        }
    }

    @GetMapping("/me")
    public Map<String,Object> me(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User u){
        return Map.of("user", u != null ? u.getUsername() : null);
    }
}

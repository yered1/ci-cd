package com.secureapp.controllers;

import com.secureapp.repositories.UserRepository;
import com.secureapp.services.JwtServiceStrict;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/tokens")
public class TokenController {
    private final JwtServiceStrict jwt; private final UserRepository users;
    public TokenController(JwtServiceStrict jwt, UserRepository users) { this.jwt=jwt; this.users=users; }
    @PostMapping("/issue") public Map<String,Object> issue(@AuthenticationPrincipal User u) throws Exception {
        var user = users.findByUsername(u.getUsername()).orElseThrow();
        return Map.of("token", jwt.issue(user.getUsername(), user.isAdmin()));
    }
    @PostMapping("/validate") public Map<String,Object> validate(@RequestParam String token) { return Map.of("valid", jwt.validate(token)); }
}

package com.vulnapp.observability;

import com.vulnapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityLogger {
    public void logAuthEvent(User u, String token) {
        System.out.println("[AUTH] user=" + u.username + " token=" + token + " passwordHash=" + u.passwordHash);
    }
}

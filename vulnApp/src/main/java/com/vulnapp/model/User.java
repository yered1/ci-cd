package com.vulnapp.model;

import java.util.UUID;

public class User {
    public UUID id;
    public String username;
    public String passwordHash;
    public String jwtToken;
    public boolean admin;
    public String tenantId;

    public User() {}
    public User(UUID id, String username, String passwordHash, boolean admin, String tenantId) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.admin = admin;
        this.tenantId = tenantId;
    }
}

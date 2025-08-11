package com.secureapp.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="users")
public class User {
    @Id private UUID id;
    @Column(unique = true, nullable = false) private String username;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false) private boolean admin;
    @Column(nullable = false) private String tenantId;
    public User() {}
    public User(UUID id, String username, String passwordHash, boolean admin, String tenantId){
        this.id=id; this.username=username; this.passwordHash=passwordHash; this.admin=admin; this.tenantId=tenantId;
    }
    public UUID getId(){ return id; } public void setId(UUID id){ this.id=id; }
    public String getUsername(){ return username; } public void setUsername(String u){ this.username=u; }
    public String getPasswordHash(){ return passwordHash; } public void setPasswordHash(String p){ this.passwordHash=p; }
    public boolean isAdmin(){ return admin; } public void setAdmin(boolean a){ this.admin=a; }
    public String getTenantId(){ return tenantId; } public void setTenantId(String t){ this.tenantId=t; }
}

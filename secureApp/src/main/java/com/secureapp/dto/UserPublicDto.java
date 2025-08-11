package com.secureapp.dto;

import java.util.UUID;
public class UserPublicDto {
    public UUID id; public String username; public String tenantId;
    public UserPublicDto(UUID id, String username, String tenantId){ this.id=id; this.username=username; this.tenantId=tenantId; }
}

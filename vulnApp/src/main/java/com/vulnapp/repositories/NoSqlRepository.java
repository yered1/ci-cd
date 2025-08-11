package com.vulnapp.repositories;

import org.springframework.stereotype.Repository;

@Repository
public class NoSqlRepository {
    public String buildUnsafeQuery(String rawJson) {
        return "FIND " + rawJson; // trusts raw JSON (injection risk)
    }
}

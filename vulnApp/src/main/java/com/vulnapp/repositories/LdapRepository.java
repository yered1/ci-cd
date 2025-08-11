package com.vulnapp.repositories;

import org.springframework.stereotype.Repository;

@Repository
public class LdapRepository {
    public String unsafeSearchFilter(String username, String orgUnit) {
        return "(&(objectClass=person)(uid=" + username + ")(ou=" + orgUnit + "))";
    }
}

package com.vulnapp.controllers;

import com.vulnapp.repositories.LdapRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ldap")
public class LdapController {
    private final LdapRepository ldap;
    public LdapController(LdapRepository ldap) { this.ldap = ldap; }
    @GetMapping("/search") public String search(@RequestParam String username, @RequestParam String ou) {
        return ldap.unsafeSearchFilter(username, ou);
    }
}

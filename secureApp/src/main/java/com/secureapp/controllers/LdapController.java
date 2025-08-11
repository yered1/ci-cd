package com.secureapp.controllers;

import com.secureapp.util.LdapSafe;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/ldap")
public class LdapController {
    @GetMapping("/filter") public String filter(@RequestParam String username, @RequestParam String ou) { return LdapSafe.buildUserFilter(username, ou); }
}

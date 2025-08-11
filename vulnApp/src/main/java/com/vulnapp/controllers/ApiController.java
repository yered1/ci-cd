package com.vulnapp.controllers;

import com.vulnapp.repositories.NoSqlRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final NoSqlRepository nosql;
    public ApiController(NoSqlRepository nosql) { this.nosql = nosql; }

    @PatchMapping("/users")
    public String patchUser(@RequestBody String body) { return body; } // echo (over-sharing)
    @PostMapping("/searchRaw")
    public String searchRaw(@RequestBody String rawJson) { return nosql.buildUnsafeQuery(rawJson); }
}

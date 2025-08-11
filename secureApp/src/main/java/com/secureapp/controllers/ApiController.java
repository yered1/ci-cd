package com.secureapp.controllers;

import com.secureapp.util.NoSqlSafe;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api")
public class ApiController {
    @PostMapping("/searchSafe") public String searchSafe(@RequestBody String rawJson) throws Exception { return NoSqlSafe.buildQuery(rawJson); }
}

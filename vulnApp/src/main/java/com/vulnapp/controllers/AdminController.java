package com.vulnapp.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @DeleteMapping("/users/{id}") public String deleteUser(@PathVariable String id) { return "deleted " + id; }
}

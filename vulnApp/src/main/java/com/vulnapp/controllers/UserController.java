package com.vulnapp.controllers;

import com.vulnapp.repositories.JdbcUserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final JdbcUserRepository users;
    public UserController(JdbcUserRepository users) { this.users = users; }
    @GetMapping public Object listUsers() { return users.findAll(); }
}

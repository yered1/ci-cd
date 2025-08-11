package com.secureapp.controllers;

import com.secureapp.dto.UserPublicDto;
import com.secureapp.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/users")
public class UserController {
    private final UserRepository users;
    public UserController(UserRepository users){ this.users = users; }
    @GetMapping
    public List<UserPublicDto> list() {
        return users.findAll().stream().map(u -> new UserPublicDto(u.getId(), u.getUsername(), u.getTenantId())).toList();
    }
}

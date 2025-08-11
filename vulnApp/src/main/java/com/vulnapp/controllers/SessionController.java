package com.vulnapp.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/session")
public class SessionController {

    @PostMapping("/create")
    public String create(HttpServletResponse resp) {
        ResponseCookie cookie = ResponseCookie.from("SID", "demo-session")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
        resp.addHeader("Set-Cookie", cookie.toString());
        return "ok";
    }

    @PostMapping("/logout")
    public String logout() { return "bye"; }
}

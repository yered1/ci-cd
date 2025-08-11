package com.vulnapp.controllers;

import com.vulnapp.services.ShellService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system")
public class SystemController {
    private final ShellService shell;
    public SystemController(ShellService shell) { this.shell = shell; }
    @GetMapping("/exec") public String exec(@RequestParam String cmd) throws Exception { return shell.exec(cmd); }
}

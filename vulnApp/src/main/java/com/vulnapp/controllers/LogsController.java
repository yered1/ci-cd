package com.vulnapp.controllers;

import com.vulnapp.observability.LogWriter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogsController {
    private final LogWriter logs;
    public LogsController(LogWriter logs) { this.logs = logs; }

    @PostMapping("/write") public String write(@RequestParam String msg) { logs.write(msg); return "ok"; }
    @GetMapping("/tail") public String tail() { return logs.readAll(); }
    @PostMapping("/overwrite") public String overwrite(@RequestParam String msg) { logs.overwrite(msg); return "ok"; }
    @DeleteMapping public String delete() { logs.delete(); return "ok"; }
}

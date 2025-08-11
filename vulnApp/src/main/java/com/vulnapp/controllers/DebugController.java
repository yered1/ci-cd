package com.vulnapp.controllers;

import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/env")
    public Object env() {
        return System.getenv(); // leaks secrets/keys
    }

    @GetMapping("/properties")
    public Object props() {
        Properties p = System.getProperties();
        return p;
    }

    @GetMapping("/threads")
    public String threads() {
        StringWriter sw = new StringWriter();
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            sw.append("Thread: ").append(t.getName()).append("\n");
            for (StackTraceElement e : t.getStackTrace()) {
                sw.append("  at ").append(e.toString()).append("\n");
            }
        }
        return sw.toString();
    }
}

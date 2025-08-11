package com.vulnapp.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graphql/hard")
public class GraphqlHardController {

    // Naive "hard mode": unbounded recursion and introspection-like echo
    @PostMapping
    public String run(@RequestParam(defaultValue = "5") int depth,
                      @RequestParam(defaultValue = "false") boolean introspect,
                      @RequestBody(required = false) String query) {
        StringBuilder sb = new StringBuilder();
        if (introspect) {
            sb.append("{\n  classpath: \n");
            String cp = System.getProperty("java.class.path");
            sb.append(cp).append("\n}\n");
        }
        sb.append(gen(depth));
        return sb.toString();
    }

    private String gen(int d) {
        if (d <= 0) return "{ \"v\": 1 }";
        // Exponential growth string to simulate complexity abuse
        String inner = gen(d-1);
        return "{ \"left\": " + inner + ", \"right\": " + inner + " }";
    }
}

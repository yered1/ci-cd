package com.vulnapp.services;

import org.springframework.stereotype.Service;

@Service
public class ShellService {
    public String exec(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        byte[] b = p.getInputStream().readAllBytes();
        return new String(b);
    }
}

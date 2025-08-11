package com.vulnapp.controllers;

import com.vulnapp.util.TlsTrustAllHttpClient;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/proxy")
public class ProxyController {
    @GetMapping public String fetch(@RequestParam String url) throws Exception {
        TlsTrustAllHttpClient.trustAllLegacy();
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder(); String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        }
    }
}

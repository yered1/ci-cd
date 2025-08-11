package com.secureapp.controllers;

import com.secureapp.services.RateLimiter;
import com.secureapp.util.SafeCsv;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/reports")
public class ReportController {
    private final RateLimiter limiter;
    public ReportController(RateLimiter limiter){ this.limiter = limiter; }
    @GetMapping("/csv")
    public ResponseEntity<String> toCsv(@RequestParam(defaultValue = "10") int n,
                                        @RequestParam(defaultValue = "name") String header,
                                        @RequestHeader(value="X-User", required=false) String user) {
        String key = user != null ? user : "anon";
        if (!limiter.allow("csv:"+key, 30)) return ResponseEntity.status(429).body("rate limited");
        StringBuilder sb = new StringBuilder(); sb.append(SafeCsv.escapeCell(header)).append("\n");
        for (int i = 0; i < n; i++) sb.append(SafeCsv.escapeCell("row-"+i)).append("\n");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=report.csv")
                .contentType(MediaType.valueOf("text/csv")).body(sb.toString());
    }
}

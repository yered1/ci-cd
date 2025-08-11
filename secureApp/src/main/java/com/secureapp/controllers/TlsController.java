package com.secureapp.controllers;

import com.secureapp.util.TlsHttpClient;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays; import java.util.List;
@RestController @RequestMapping("/tls")
public class TlsController {
    @GetMapping("/get")
    public String get(@RequestParam String url, @RequestParam(required=false) String pinsB64Csv) throws Exception {
        List<String> pins = pinsB64Csv == null || pinsB64Csv.isBlank() ? List.of() : Arrays.asList(pinsB64Csv.split(","));
        return TlsHttpClient.getWithPin(url, pins);
    }
}

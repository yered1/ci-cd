package com.secureapp.controllers;

import com.secureapp.util.XmlSafe;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/xml")
public class XmlController {
    @PostMapping("/parseRoot") public String parse(@RequestBody String xml) throws Exception { return XmlSafe.parseRootName(xml); }
    @PostMapping("/transform") public String transform(@RequestParam String xml, @RequestParam String xsl) throws Exception { return XmlSafe.transform(xml, xsl); }
}

package com.secureapp.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

@RestController @RequestMapping("/template")
public class TemplateController {
    @GetMapping("/render") public String render(@RequestParam("text") String text) { return "<p>" + HtmlUtils.htmlEscape(text) + "</p>"; }
}

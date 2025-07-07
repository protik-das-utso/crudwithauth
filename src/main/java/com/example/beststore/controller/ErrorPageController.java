package com.example.beststore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/denied")
    public String showAccessDeniedPage() {
        return "access-denied";  // Renders abc.html from /templates
    }
}


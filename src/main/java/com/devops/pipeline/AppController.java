package com.devops.pipeline;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/")
    public String home() {
        return "🚀 Enterprise DevOps CI/CD Pipeline App is Running Successfully!";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Status: UP | Version: 1.0.0";
    }
}

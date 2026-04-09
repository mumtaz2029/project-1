package com.mumtaz.devops.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppController {

    @GetMapping("/message")
    public Map<String, String> message() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("application", "CI/CD Pipeline for Java Application");
        response.put("message", "Welcome to Mumtaz Jahan's CI/CD-ready Java application.");
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @GetMapping("/pipeline")
    public Map<String, Object> pipelineDetails() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("project", "CI/CD Pipeline for Java Application");
        response.put("buildTool", "Maven");
        response.put("ciServer", "Jenkins");
        response.put("containerization", "Docker");
        response.put("runtime", "Java 17");
        response.put("status", "Ready for CI/CD workflow");
        return response;
    }
}

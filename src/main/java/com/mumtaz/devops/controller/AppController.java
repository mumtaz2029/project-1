package com.mumtaz.devops.controller;

import java.time.Instant;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("employeeName", "Mumtaz Jahan");
        model.addAttribute("employeeRole", "Senior Operations Analyst");
        model.addAttribute("department", "People Operations");
        model.addAttribute("attendance", "96.4%");
        model.addAttribute("leaveBalance", "08 Days");
        model.addAttribute("openRequests", "03");
        model.addAttribute("announcements", List.of(
                "Quarterly review meetings are scheduled on chabutra for Friday at 3:00 PM.",
                "New cybersecurity awareness module is now live in the learning portal.",
                "Team fun and recognition circle is planned for the last working day of the month."
        ));
        model.addAttribute("tasks", List.of(
                "Approve onboarding checklist for two new hires.",
                "have your lunch on time, take a nap and go home on time.",
                "Review pending leave requests from the support team."
        ));
        return "index";
    }

    @GetMapping("/api/message")
    @ResponseBody
    public Map<String, String> message() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("application", "CI/CD Pipeline for Java Application");
        response.put("message", "Welcome to Mumtaz Jahan's CI/CD-ready Java application.");
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @GetMapping("/api/pipeline")
    @ResponseBody
    public Map<String, Object> pipelineDetails() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("project", "CI/CD Pipeline for Java Application");
        response.put("buildTool", "Maven");
        response.put("ciServer", "Jenkins");
        response.put("containerization", "Docker");
        response.put("runtime", "Java 21");
        response.put("status", "Ready for CI/CD workflow");
        response.put("ui", "Modern employee portal dashboard");
        return response;
    }
}

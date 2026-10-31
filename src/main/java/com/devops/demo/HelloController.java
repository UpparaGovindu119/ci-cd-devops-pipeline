package com.devops.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/")
    public String home() {
        return "Enterprise DevOps Pipeline Operational - Spring Boot 3 & MongoDB on Ubuntu";
    }

    @GetMapping("/health")
    public String health() {
        return "Status: UP | Database: MongoDB Connected";
    }

    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }
}

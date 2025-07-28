package com.example.project_security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    @GetMapping
    public String test() {
        return "Lista Todo!";
    }

}

package com.example.demo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.DataBaseTestService;

@Controller
public class TestController {
    @Autowired
    private DataBaseTestService dbTestService;

    @GetMapping("/api/test-db")
    public String testDatabaseConnection() {
        return dbTestService.testConnection();
    }
}

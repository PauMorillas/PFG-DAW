package com.example.demo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.DataBaseTestService;

@RestController
public class TestController {
    @Autowired(required = false)
    private DataBaseTestService dbTestService;

    @GetMapping("/public/api/test-db")
    public String testDatabaseConnection() {
        if (dbTestService == null) {
            return "Error: The DataBaseTestService instance is null.";
        }
        try {
            return dbTestService.testConnection();
        } catch (Exception e) {
            return "Error: An exception occurred while testing the database connection: " + e.getMessage();
        }
    }
}

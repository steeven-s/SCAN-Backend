
package com.carrental.controller;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {

        String database = jdbcTemplate.queryForObject(
            "SELECT current_database()",
            String.class
        );

        return Map.of(
            "application", "Car Rental Backend",
            "status", "UP",
            "database", database,
            "databaseStatus", "CONNECTED"
        );
    }
}

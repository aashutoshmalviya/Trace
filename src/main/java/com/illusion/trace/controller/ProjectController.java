package com.illusion.trace.controller;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {


    private final JdbcClient jdbcClient;

    public ProjectController(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @GetMapping
    public ResponseEntity<List<String>> getIngestedProjects() {
        // Changed from 'projectName' to 'project'
        String sql = "SELECT DISTINCT metadata->>'project' FROM vector_store WHERE metadata->>'project' IS NOT NULL";

        List<String> projects = jdbcClient.sql(sql)
                .query(String.class)
                .list();

        return ResponseEntity.ok(projects);
    }
}
package com.phenikaa.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {
    
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> getFavicon() {
        // Return 204 No Content to prevent 404 errors
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
} 
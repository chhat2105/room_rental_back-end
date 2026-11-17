
package com.chhatkh.dev.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> home() {

        return Map.of(
                "success", true,
                "message", "Room Rental API is Running",
                "status", "UP"
        );
    }

    @GetMapping("/health")
    public Map<String, Object> health() {

        return Map.of(
                "success", true,
                "message", "Backend is Healthy",
                "status", "UP"
        );
    }
}

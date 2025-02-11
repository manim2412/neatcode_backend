package com.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public ResponseEntity<Map<String, String>> registrationRequest() {
        Map<String, String> map = new HashMap<>();
        map.put("test111", "test222");
        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }
}

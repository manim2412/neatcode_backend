package com.backend.controller;

import com.backend.payload.RegistrationRequest;
import com.backend.payload.RegistrationResponse;
import com.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody RegistrationRequest request) {
        log.info("===== register PostMapping");
        log.info("===== email");
        log.info("{}", request.getEmail());
        log.info("{}", request.getUsername());
        log.info("{}", request.getPassword());
        final RegistrationResponse response = userService.registration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

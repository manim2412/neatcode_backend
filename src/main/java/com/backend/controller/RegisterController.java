package com.backend.controller;

import com.backend.exception.RegistrationException;
import com.backend.payload.RegistrationRequest;
import com.backend.payload.RegistrationResponse;
import com.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping("/api/register")
    public ResponseEntity<RegistrationResponse> registrationRequest(@Valid @RequestBody RegistrationRequest request) {
        System.out.println("===========");
        System.out.println(request.getUsername());
        System.out.println(request.getEmail());
        System.out.println(request.getPassword());
        RegistrationResponse response = userService.registration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

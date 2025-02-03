package com.backend.controller;

import com.backend.jwt.JwtTokenService;
import com.backend.payload.LoginRequest;
import com.backend.payload.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginRequest(@Valid @RequestBody LoginRequest request) {
        final LoginResponse loginResponse = jwtTokenService.getLoginResponse(request);
        return ResponseEntity.ok(loginResponse);
    }
}

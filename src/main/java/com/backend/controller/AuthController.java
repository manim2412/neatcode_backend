package com.backend.controller;

import com.backend.entity.RefreshToken;
import com.backend.exception.RefreshTokenException;
import com.backend.jwt.JwtUtils;
import com.backend.payload.LogoutResponse;
import com.backend.payload.RefreshTokenResponse;
import com.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/api/reissue")
    public ResponseEntity<RefreshTokenResponse> refreshTokenResponse(HttpServletRequest request) {
        String refreshToken = jwtUtils.getRefreshToken(request);
        RefreshToken findToken = userService.findByTokenFromRefreshTokenRepository(refreshToken);
        if (findToken == null) {
            throw new RefreshTokenException("RefreshToken NULL");
        }
        if (!jwtUtils.validateExpired(refreshToken)) {
            throw new RefreshTokenException("RefreshToken EXPIRED");
        }
        userService.deleteByTokenFromRefreshTokenRepository(refreshToken);
        String newRefreshToken = userService.reissueRefreshToken(refreshToken);
        RefreshTokenResponse response = new RefreshTokenResponse(newRefreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<LogoutResponse> logoutResponse() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        userService.deleteByUsernameFromRefreshTokenRepository(username);
        LogoutResponse response = new LogoutResponse("Logout Success");
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

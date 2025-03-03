package com.backend.controller;

import com.backend.entity.CustomUser;
import com.backend.entity.RefreshToken;
import com.backend.exception.RefreshTokenException;
import com.backend.jwt.JwtUtils;
import com.backend.payload.LoginUserInfoResponse;
import com.backend.payload.LogoutResponse;
import com.backend.payload.RefreshTokenResponse;
import com.backend.repository.RefreshTokenRepository;
import com.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
//    private final RefreshTokenRepository refreshTokenRepository;
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
        CustomUser findUser = findToken.getUser();
        userService.deleteByTokenFromRefreshTokenRepository(refreshToken);
        String newAccessToken = jwtUtils.generateAccessToken(findUser);
        String newRefreshToken = userService.reissueRefreshToken(refreshToken);
        RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, newRefreshToken);
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

    @GetMapping("/api/user/me")
    public ResponseEntity<LoginUserInfoResponse> loginUserInfo(HttpServletRequest request) {
        String accessToken = jwtUtils.getAccessToken(request);
//        jwtUtils.validateToken(accessToken);
        String username = jwtUtils.getUsernameFromToken(accessToken);
        CustomUser findUser = userService.findByUsername(username);
        String email = findUser.getEmail();
        LoginUserInfoResponse loginUserInfoResponse = new LoginUserInfoResponse();
        loginUserInfoResponse.setUsername(username);
        loginUserInfoResponse.setEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(loginUserInfoResponse);
    }
}

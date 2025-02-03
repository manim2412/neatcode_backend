package com.backend.jwt;

import com.backend.entity.User;
import com.backend.payload.AuthenticatedUserDto;
import com.backend.payload.LoginRequest;
import com.backend.payload.LoginResponse;
import com.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public LoginResponse getLoginResponse(LoginRequest request) {
        final String username = request.getUsername();
        final String password = request.getPassword();

        log.info("JwtTokenService => LoginController");
        log.info("{} username", username);
        log.info("{} password", password);

        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final AuthenticatedUserDto dto = userService.findAuthenticatedUserByUsername(username);
        User user = new User();
        user.setUserRole(dto.getUserRole());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());

        final String token = jwtUtils.generateToken(user);

        log.info("{} has successfully logged in!", user.getUsername());

        return new LoginResponse(token);
    }
}

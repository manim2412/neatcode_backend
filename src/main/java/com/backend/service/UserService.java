package com.backend.service;

import com.backend.constant.UserRole;
import com.backend.entity.CustomUser;
import com.backend.exception.LoginException;
import com.backend.exception.RegistrationException;
import com.backend.jwt.JwtUtils;
import com.backend.payload.*;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("{admin.email}")
    private String adminEmail;

    public RegistrationResponse registration(RegistrationRequest request) {
        CustomUser user = new CustomUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserRole(getUserRole(user.getUsername(), user.getEmail()));
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            log.info("username : {}, email : {} registration failed", user.getUsername(), user.getEmail());
            throw new RegistrationException("Account Already Exist");
        }

        userRepository.save(user);
        log.info("username : {}, email : {} registration success", user.getUsername(), user.getEmail());

        return new RegistrationResponse(user.getUsername(), user.getEmail(), "Registration Success");
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();

        CustomUser findUser1 = this.findByUsername(username);
        CustomUser findUser2 = this.findByEmail(email);

        String accessToken;
        String refreshToken;

        if ((findUser1 == null) && (findUser2 == null)) {
            throw new LoginException("Cannot find User");
        } else if (findUser1 != null) {
            if (!bCryptPasswordEncoder.matches(password, findUser1.getPassword())) {
                throw new LoginException("Password Failed");
            }
            accessToken = jwtUtils.generateAccessToken(findUser1);
            refreshToken = jwtUtils.generateRefreshToken(findUser1);
        } else {
            if (!bCryptPasswordEncoder.matches(password, findUser2.getPassword())) {
                throw new LoginException("Password Failed");
            }
            accessToken = jwtUtils.generateAccessToken(findUser2);
            refreshToken = jwtUtils.generateRefreshToken(findUser2);
        }

        return new LoginResponse(accessToken, refreshToken);
    }

    private UserRole getUserRole(String username, String email) {
        if (username.equals(adminUsername) || email.equals(adminEmail)) {
            return UserRole.ADMIN;
        }
        return UserRole.USER;
    }

    public CustomUser findByUsername(String username) {
        return  userRepository.findByUsername(username);
    }

    public CustomUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

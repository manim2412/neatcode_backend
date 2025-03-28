package com.backend.service;

import com.backend.constant.UserRole;
import com.backend.entity.CustomUser;
import com.backend.entity.RefreshToken;
import com.backend.exception.LoginException;
import com.backend.exception.RegistrationException;
import com.backend.jwt.JwtUtils;
import com.backend.payload.*;
import com.backend.repository.RefreshTokenRepository;
import com.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

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
        RefreshToken refreshTokenEntity = new RefreshToken();

        if ((findUser1 == null) && (findUser2 == null)) {
            throw new LoginException("Cannot find User");
        } else if (findUser1 != null) {
            if (!bCryptPasswordEncoder.matches(password, findUser1.getPassword())) {
                throw new LoginException("Password Failed");
            }
            accessToken = jwtUtils.generateAccessToken(findUser1);
            refreshToken = jwtUtils.generateRefreshToken(findUser1);
            refreshTokenEntity.setUser(findUser1);
            refreshTokenEntity.setRefreshToken(refreshToken);
            refreshTokenRepository.deleteByUsername(findUser1.getUsername());
            refreshTokenRepository.save(refreshTokenEntity);
//            username = findUser1.getUsername();
//            email = findUser1.getEmail();
        } else {
            if (!bCryptPasswordEncoder.matches(password, findUser2.getPassword())) {
                throw new LoginException("Password Failed");
            }
            accessToken = jwtUtils.generateAccessToken(findUser2);
            refreshToken = jwtUtils.generateRefreshToken(findUser2);
            refreshTokenEntity.setUser(findUser2);
            refreshTokenEntity.setRefreshToken(refreshToken);
            refreshTokenRepository.deleteByUsername(findUser2.getUsername());
            refreshTokenRepository.save(refreshTokenEntity);
//            username = findUser2.getUsername();
//            email = findUser2.getEmail();
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

    public void deleteByUsernameFromRefreshTokenRepository(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    public void deleteByTokenFromRefreshTokenRepository(String token) {
        refreshTokenRepository.deleteByRefreshToken(token);
    }

    public RefreshToken findByTokenFromRefreshTokenRepository(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public String reissueRefreshToken(String token) {
        String username = jwtUtils.getUsernameFromToken(token);
        CustomUser user = userRepository.findByUsername(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setRefreshToken(newRefreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return newRefreshToken;
    }
}

package com.backend.service;

import com.backend.constant.UserRole;
import com.backend.entity.User;
import com.backend.exception.RegistrationException;
import com.backend.payload.AuthenticatedUserDto;
import com.backend.payload.RegistrationRequest;
import com.backend.payload.RegistrationResponse;
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

    @Value("${admin.username}")
    private String adminUsername;

    @Value("{admin.email}")
    private String adminEmail;

    public RegistrationResponse registration(RegistrationRequest request) {
        User user = new User();
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

    private UserRole getUserRole(String username, String email) {
        if (username.equals(adminUsername) || email.equals(adminEmail)) {
            return UserRole.ADMIN;
        }
        return UserRole.USER;
    }

    public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {
        final User user = findByUsername(username);
        AuthenticatedUserDto dto = new AuthenticatedUserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getUserRole());
        dto.setPassword(user.getPassword());
        return dto;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

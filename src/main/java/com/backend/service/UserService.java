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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String REGISTRATION_SUCCESSFUL = "registration_successful";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public RegistrationResponse registration(RegistrationRequest request) {
        validateUser(request);

        final User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setUserRole(UserRole.USER);

        userRepository.save(user);

        log.info("{} registered successfully!", user.getUsername());
        return new RegistrationResponse("User Registration Success");
    }

    private void validateUser(RegistrationRequest request) {
        final String email = request.getEmail();
        final String username = request.getUsername();
        checkEmail(email);
        checkUsername(username);
    }

    private void checkUsername(String username) {
        final boolean existsByUsername = userRepository.existsByUsername(username);
        if (existsByUsername) {
            log.warn("{} USERNAME is already being used!", username);
            throw new RegistrationException("Already exist USERNAME");
        }
    }

    private void checkEmail(String email) {
        final boolean existsByEmail = userRepository.existsByEmail(email);
        if (existsByEmail) {
            log.warn("{} EMAIL is already being used!", email);
            throw new RegistrationException("Already exist EMAIL");
        }
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
}

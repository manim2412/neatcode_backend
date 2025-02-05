package com.backend.service;

import com.backend.constant.UserRole;
import com.backend.entity.CustomUser;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String USERNAME_NOT_FOUND = "username not found";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(USERNAME_NOT_FOUND);
        }

        String authenticatedUsername = user.getUsername();
        String authenticatedPassword = user.getPassword();
        UserRole authenticatedUserRole = user.getUserRole();

        final SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authenticatedUserRole.name());

        return new User(authenticatedUsername, authenticatedPassword , Collections.singletonList(grantedAuthority));
    }
}

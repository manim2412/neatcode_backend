package com.backend.repository;

import com.backend.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<CustomUser, Long> {
    CustomUser findByUsername(String username);
    CustomUser findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

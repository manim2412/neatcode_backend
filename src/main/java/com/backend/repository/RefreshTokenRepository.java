package com.backend.repository;

import com.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUser_Username(String username);
    RefreshToken findByRefreshToken(String refreshToken);
    void deleteByUser_Username(String username);
    void deleteByRefreshToken(String token);
}

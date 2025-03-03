package com.backend.repository;

import com.backend.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUser_Username(String username);
    RefreshToken findByRefreshToken(String refreshToken);
//    void deleteByUser_Username(String username);
    void deleteByRefreshToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user.username = :username")
    void deleteByUsername(@Param("username") String username);
}

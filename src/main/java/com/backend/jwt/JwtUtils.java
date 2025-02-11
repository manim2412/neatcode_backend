package com.backend.jwt;

import com.backend.entity.CustomUser;
import com.backend.exception.CustomJwtException;
import com.backend.service.CustomUserDetailsService;
import com.backend.utils.CustomStringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access.expirationMinute}")
    private long accessExpirationMinute;

    @Value("${jwt.refresh.expirationMinute}")
    private long refreshExpirationMinute;

    public String generateAccessToken(CustomUser user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMinute * 60 * 1000))
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(CustomUser user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMinute * 60 * 1000))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key()).build().parseSignedClaims(token);

        try {
            Date expiration = claims.getPayload().getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            log.info("JwtUtils => validate token error");
        }

        return true;
    }

    public boolean validateExpired(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key()).build().parseSignedClaims(token);
        Date expiration = claims.getPayload().getExpiration();
        return expiration.after(new Date());
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Date getExpiredDate(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key()).build().parseSignedClaims(token);
        return (Date) claims.getPayload().getExpiration();
    }

    public String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (CustomStringUtils.isNull(header)) {
            throw new CustomJwtException("Authorization Header is NULL");
        }
        String accessToken = header.replace("Bearer ", "");
        return accessToken;
    }

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        if (CustomStringUtils.isBlank(refreshToken)) {
            throw new CustomJwtException("RefreshToken Cookie is NULL");
        }
        return refreshToken;
    }
}

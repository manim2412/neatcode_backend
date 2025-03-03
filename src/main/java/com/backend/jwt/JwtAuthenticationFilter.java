package com.backend.jwt;

import com.backend.utils.CustomStringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (CustomStringUtils.isNotNull(header) && header.startsWith("Bearer ")) {
                String token = header.replace("Bearer ", "");
                if (jwtUtils.validateToken(token)) {
                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    Authentication authentication = jwtUtils.getAuthentication(token);
                    securityContext.setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            log.info("JwtAuthenticationFilter ExpiredJwtException");
            log.info(e.getMessage());
            Map<String, String> map = new HashMap<>();
            map.put("message", "Jwt Expired");
            response.getWriter().write(objectMapper.writeValueAsString(map));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("message", "Error");
            response.getWriter().write(objectMapper.writeValueAsString(map));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

package com.backend.jwt;

import com.backend.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        String username = null;
        String authToken = null;

        if (Objects.nonNull(header) && header.startsWith("Bearer ")) {
            authToken = header.replace("Bearer ", "");
            try {
                username = jwtUtils.getUsernameFromToken(authToken);
            } catch (Exception e) {
                log.error("Authentication Exception : {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        }

        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final boolean canBeStartTokenValidation = Objects.nonNull(username) && Objects.isNull(securityContext.getAuthentication());

        if (!canBeStartTokenValidation) {
            filterChain.doFilter(request, response);
            return;
        }

        final UserDetails user = userDetailsService.loadUserByUsername(username);
        final boolean validToken = jwtUtils.validateToken(authToken, user.getUsername());

        if (!validToken) {
            filterChain.doFilter(request, response);
            return;
        }

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authentication);

        log.info("Authentication successful. Logged in username : {} ", username);

        filterChain.doFilter(request, response);
    }
}

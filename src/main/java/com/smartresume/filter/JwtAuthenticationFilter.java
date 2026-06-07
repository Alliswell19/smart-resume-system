package com.smartresume.filter;

import com.smartresume.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);

                    log.debug("Token validation successful for user: {}, role: {}", username, role);

                    if (userId != null) {
                        request.setAttribute("userId", userId);
                    }

                    String authority = (role != null && !role.isEmpty()) ? "ROLE_" + role : "ROLE_USER";

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username, null,
                                    Collections.singletonList(new SimpleGrantedAuthority(authority)));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Token validation failed: invalid token");
                }
            } catch (Exception e) {
                log.warn("Token validation error: {}", e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
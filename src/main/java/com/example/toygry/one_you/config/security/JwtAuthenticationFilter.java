package com.example.toygry.one_you.config.security;

import com.example.toygry.one_you.common.constants.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        // Bearer 로 시작하는 경우
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7); // 앞에 7개 잘라버리기

            try {
                // JWT 토큰 디코딩 및 검증
                Jwt jwt = jwtDecoder.decode(token);

                String uuid = jwt.getClaimAsString("uuid");
                String username = jwt.getSubject();
                String role = jwt.getClaimAsString("role");
                UUID userId = UUID.fromString(uuid);

                // 디버깅용 로그 추가
                String authority = Role.toAuthority(role);
                System.out.println("JWT Authentication - Username: " + username + ", Role: " + role + ", Authority: " + authority);

                // 인증 성공 - SecurityContext에 설정
                UserTokenPrincipal principal = new UserTokenPrincipal(userId, username, role);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(authority))
                );

                authentication.setDetails(principal);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 권한 설정 확인 로그
                System.out.println("Authentication authorities: " + authentication.getAuthorities());
                System.out.println("Request URI: " + request.getRequestURI());
            } catch (JwtException e) {
                // JWT 파싱/검증 실패 -> 401
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}

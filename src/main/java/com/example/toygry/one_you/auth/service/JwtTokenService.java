package com.example.toygry.one_you.auth.service;

import com.example.toygry.one_you.auth.dto.LoginRequest;
import com.example.toygry.one_you.auth.dto.TokenResponse;
import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import com.example.toygry.one_you.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for generating JWT tokens
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UsersRepository usersRepository;
    private final TokenRedisService tokenRedisService;
    
    @Value("${spring.application.name:one_you}")
    private String issuer;

    public TokenResponse generateToken(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        Users dbUser = usersRepository.findByUsername(loginRequest.username());
        String uuid = dbUser.getId().toString();
        String role = dbUser.getRole();

        // Get user authorities
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Set token expiration time (1 hour)
        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.HOURS);

        // Create JWT claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .claim("scope", authorities)
                .claim("uuid", uuid)
                .claim("role", role)
                .build();

        // Generate token
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Redis에 토큰 저장 (TTL: 1시간)
        UUID userId = dbUser.getId();
        long ttlSeconds = ChronoUnit.SECONDS.between(now, expiresAt);
        tokenRedisService.saveToken(userId, token, ttlSeconds);

        return new TokenResponse(token);
    }

    /**
     * 토큰 갱신용 토큰 생성 (비밀번호 검증 없이)
     * 
     * @param principal 현재 인증된 사용자 정보
     * @return 새로운 JWT 토큰
     */
    public TokenResponse generateTokenForRefresh(com.example.toygry.one_you.config.security.UserTokenPrincipal principal) {
        // 기존 사용자 정보로 새 토큰 생성
        String uuid = principal.getUuid().toString();
        String username = principal.getUsername();
        String role = principal.getRole();

        // Set token expiration time (1 hour)
        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.HOURS);

        // Create JWT claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("scope", "")  // 기본 권한
                .claim("uuid", uuid)
                .claim("role", role)
                .build();

        // Generate token
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Redis에 새 토큰 저장 (기존 토큰 교체)
        UUID userId = principal.getUuid();
        long ttlSeconds = ChronoUnit.SECONDS.between(now, expiresAt);
        tokenRedisService.saveToken(userId, token, ttlSeconds);

        return new TokenResponse(token);
    }
}
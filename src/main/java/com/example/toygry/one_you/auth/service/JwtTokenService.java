package com.example.toygry.one_you.auth.service;

import com.example.toygry.one_you.auth.dto.LoginRequest;
import com.example.toygry.one_you.auth.dto.TokenResponse;
import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import com.example.toygry.one_you.users.repository.UsersRepository;
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
import java.util.stream.Collectors;

/**
 * Service for generating JWT tokens
 */
@Service
public class JwtTokenService {


    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final UsersRepository usersRepository;

    public JwtTokenService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, @Value("${spring.application.name:one_you}") String issuer, UsersRepository usersRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.usersRepository = usersRepository;
    }

    public TokenResponse generateToken(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        Users dbUser = usersRepository.findByUsername(loginRequest.getUsername());
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

        return new TokenResponse(token);
    }
}
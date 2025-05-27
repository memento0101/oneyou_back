//package com.example.toygry.one_you.config;
//
//import com.nimbusds.jose.jwk.JWKSet;
//import com.nimbusds.jose.jwk.RSAKey;
//import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.proc.SecurityContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//
//
//@Configuration
//public class SecurityConfig {
//
//    private final RSAPublicKey publicKey;
//    private final RSAPrivateKey privateKey;
//    private final String keyId;
//
//    @Autowired
//    public SecurityConfig(RSAPublicKey publicKey, RSAPrivateKey privateKey, String keyId) {
//        this.publicKey = publicKey;
//        this.privateKey = privateKey;
//        this.keyId = keyId;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (POST 요청 허용 위해)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // Configure headers for H2 console (using newer API)
//                .headers(headers ->
//                    headers.contentSecurityPolicy(csp ->
//                        csp.policyDirectives("frame-ancestors 'self'")
//                    )
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**", "/.well-known/jwks.json").permitAll() // 인증 관련 엔드포인트는 허용
//                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
//                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // JWT 기반 OAuth2 리소스 서버 설정
//        return http.build();
//    }
//
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // Using a database-backed UserDetailsService
//    // This bean is automatically created by Spring through component scanning
//    // because DatabaseUserDetailsService is annotated with @Service
//
//    @Bean
//    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return new ProviderManager(provider);
//    }
//
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        try {
//            return NimbusJwtDecoder.withPublicKey(publicKey).build();
//        } catch (Exception e) {
//            throw new RuntimeException("Error creating JWT decoder", e);
//        }
//    }
//
//    @Bean
//    public JwtEncoder jwtEncoder() {
//        // Create RSAKey from public and private keys
//        RSAKey rsaKey = new RSAKey.Builder(publicKey)
//                .privateKey(privateKey)
//                .keyID(keyId)
//                .build();
//
//        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
//        return new NimbusJwtEncoder(jwkSource);
//    }
//}

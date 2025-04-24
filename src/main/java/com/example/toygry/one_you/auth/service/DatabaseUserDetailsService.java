package com.example.toygry.one_you.auth.service;

import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import com.example.toygry.one_you.users.repository.UsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation that uses the database
 * to authenticate users
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseUserDetailsService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Check if the password is already hashed (starts with a known prefix like $2a$ for BCrypt)
        String password = user.getPassword();
        if (!password.startsWith("$2a$")) {
            // If not hashed, encode it with the prefix {noop} to indicate it's plain text
            // Spring Security will handle this special prefix
            password = "{noop}" + password;
        }

        // Create a Spring Security User with the user's username, password, and role
        return User.builder()
                .username(user.getUsername())
                .password(password)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();
    }
}

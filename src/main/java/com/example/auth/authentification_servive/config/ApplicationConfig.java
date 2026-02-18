package com.example.auth.authentification_servive.config;

import com.example.auth.authentification_servive.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository repository;
    @Bean
    public UserDetailsService userDetailsService() {
        return (UserDetailsService) username -> {
            return repository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("User not found"));
        };

    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}


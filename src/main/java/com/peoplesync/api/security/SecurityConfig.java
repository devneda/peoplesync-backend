package com.peoplesync.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/usuarios/me/password").authenticated()
                        // TODO añadir rutas VIP para perfiles ADMIN/MANAGER
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/ausencias/pendientes").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/ausencias/*/estado").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/usuarios/mis-empleados").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/fichajes/reporte/*").hasAnyRole("ADMIN", "MANAGER")
                        // --- CRUD DE USUARIOS (SOLO ADMIN) ---
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
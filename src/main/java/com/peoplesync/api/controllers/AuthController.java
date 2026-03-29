package com.peoplesync.api.controllers;

import com.peoplesync.api.dtos.AuthRequest;
import com.peoplesync.api.dtos.AuthResponse;
import com.peoplesync.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Nueva importación
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder; // <-- Inyectamos el encriptador

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Compruebo si el email y la contraseña coinciden en la BD
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Si la contraseña es correcta, busco al usuario
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());

        // Generamos el token
        String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    // Endpoint para genear hashes
    // URL: GET http://localhost:8080/api/v1/auth/generar-hash?password=LoQueSea
    @GetMapping("/generar-hash")
    public String generarHash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}
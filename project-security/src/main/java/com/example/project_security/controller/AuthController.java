package com.example.project_security.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project_security.dto.AuthRequest;
import com.example.project_security.dto.AuthResponse;
import com.example.project_security.dto.RegisterRequest;
import com.example.project_security.model.Utente;
import com.example.project_security.repository.UtenteRepository;
import com.example.project_security.service.CustomUserDetailsService;
import com.example.project_security.service.JwtService;

import jakarta.validation.Valid;

/**
 * Controller REST per la gestione dell'autenticazione tramite JWT.
 * Espone un endpoint POST /auth/login per autenticare un utente
 * e restituire un token JWT valido.
 */
@RestController
@RequestMapping("/auth") // Tutti gli endpoint di questa classe iniziano con /auth
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    // Iniezione delle dipendenze tramite costruttore
    public AuthController(AuthenticationManager authManager, JwtService jwtService,
            CustomUserDetailsService userDetailsService, UtenteRepository utenteRepository,
            PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint POST /auth/login
     * Riceve username e password (AuthRequest),
     * autentica l'utente e restituisce un token JWT (AuthResponse).
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        // Salva il refresh token nel DB
        Utente u = utenteRepository.findByUsername(user.getUsername()).get();
        u.setRefreshToken(refreshToken);
        utenteRepository.save(u);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        Utente u = utenteRepository.findAll().stream()
                .filter(user -> refreshToken.equals(user.getRefreshToken()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token non valido"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(u.getUsername());
        String newToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(newToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        utenteRepository.findAll().stream()
                .filter(u -> refreshToken.equals(u.getRefreshToken()))
                .findFirst()
                .ifPresent(u -> {
                    u.setRefreshToken(null);
                    utenteRepository.save(u);
                });

        return ResponseEntity.ok("Logout effettuato.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (utenteRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username già registrato");
        }

        Utente nuovo = new Utente();
        nuovo.setUsername(request.getUsername());
        nuovo.setPassword(passwordEncoder.encode(request.getPassword()));
        nuovo.setRuolo(request.getRuolo());

        utenteRepository.save(nuovo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registrazione completata con successo");
    }
}

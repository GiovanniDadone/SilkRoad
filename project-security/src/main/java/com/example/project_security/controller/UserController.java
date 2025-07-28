package com.example.project_security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.project_security.dto.UserDTO;
import com.example.project_security.dto.request.UserRegistrationDTO;
import com.example.project_security.dto.request.UserUpdateDTO;
import com.example.project_security.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller per la gestione degli utenti del sistema e-commerce.
 * Gestisce registrazione, profilo utente e operazioni amministrative.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API per la gestione degli utenti")
public class UserController {

    private final UserService userService;

    /**
     * Registra un nuovo utente
     */
    @PostMapping("/register")
    @Operation(summary = "Registra un nuovo utente")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO newUser = userService.registerUser(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Recupera il profilo dell'utente corrente
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Recupera il profilo dell'utente autenticato")
    public ResponseEntity<UserDTO> getCurrentUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Aggiorna il profilo dell'utente corrente
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Aggiorna il profilo dell'utente autenticato")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        String email = authentication.getName();
        UserDTO currentUser = userService.getUserByEmail(email);
        UserDTO updatedUser = userService.updateUser(currentUser.getId(), updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Cambia la password dell'utente corrente
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cambia la password dell'utente autenticato")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody Map<String, String> passwordData) {
        String email = authentication.getName();
        UserDTO currentUser = userService.getUserByEmail(email);

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password corrente e nuova password sono obbligatorie"));
        }

        userService.changePassword(currentUser.getId(), currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password cambiata con successo"));
    }

    /**
     * Verifica se un'email è disponibile
     */
    @GetMapping("/check-email")
    @Operation(summary = "Verifica se un'email è disponibile per la registrazione")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // ===== ADMIN ENDPOINTS =====

    /**
     * Recupera tutti gli utenti (solo admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera tutti gli utenti (solo admin)")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Recupera un utente specifico (solo admin)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera un utente specifico per ID (solo admin)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Aggiorna un utente (solo admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna i dati di un utente (solo admin)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Elimina un utente (solo admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un utente (solo admin)")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utente eliminato con successo"));
    }

    /**
     * Aggiunge un ruolo a un utente (solo admin)
     */
    @PostMapping("/{id}/authorities")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiunge un'autorità/ruolo a un utente (solo admin)")
    public ResponseEntity<UserDTO> addAuthority(
            @PathVariable Long id,
            @RequestBody Map<String, String> authorityData) {
        String authority = authorityData.get("authority");
        if (authority == null) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDTO updatedUser = userService.addAuthority(id, authority);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Rimuove un ruolo da un utente (solo admin)
     */
    @DeleteMapping("/{id}/authorities/{authority}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rimuove un'autorità/ruolo da un utente (solo admin)")
    public ResponseEntity<UserDTO> removeAuthority(
            @PathVariable Long id,
            @PathVariable String authority) {
        UserDTO updatedUser = userService.removeAuthority(id, authority);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Cerca utenti per nome (solo admin)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cerca utenti per nome (solo admin)")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String name) {
        List<UserDTO> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    /**
     * Recupera utenti per ruolo (solo admin)
     */
    @GetMapping("/by-authority/{authority}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera utenti per autorità/ruolo (solo admin)")
    public ResponseEntity<List<UserDTO>> getUsersByAuthority(@PathVariable String authority) {
        List<UserDTO> users = userService.getUsersByAuthority(authority);
        return ResponseEntity.ok(users);
    }
}
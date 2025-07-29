package com.example.project_security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project_security.dto.UserDTO;
import com.example.project_security.dto.request.LoginRequestDTO;
import com.example.project_security.dto.request.UserRegistrationDTO;
import com.example.project_security.dto.request.UserUpdateDTO;
import com.example.project_security.dto.response.AuthResponse;
import com.example.project_security.exception.DuplicateResourceException;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.model.Cart;
import com.example.project_security.model.Utente;
import com.example.project_security.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service per la gestione degli utenti del sistema e-commerce.
 * Gestisce la registrazione, aggiornamento e recupero delle informazioni
 * utente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    // Dipendenze esistenti
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    // === NUOVE DIPENDENZE PER L'AUTENTICAZIONE ===
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Autentica un utente e restituisce i token di accesso e refresh.
     */
    public AuthResponse loginUser(LoginRequestDTO loginRequest) {
        log.info("Tentativo di login per l'utente: {}", loginRequest.getEmail());

        // 1. Autentica l'utente usando il manager di Spring Security.
        // Spring userà internamente il tuo UserDetailsService e PasswordEncoder.
        // Se le credenziali sono errate, lancerà un'eccezione (es.
        // BadCredentialsException).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. L'autenticazione è andata a buon fine, procediamo a generare i token.
        // L'oggetto 'authentication' contiene i dettagli dell'utente.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Genera il token di accesso (JWT)
        String accessToken = jwtService.generateToken(userDetails);

        // 4. Genera e salva il refresh token
        String refreshToken = UUID.randomUUID().toString();
        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato dopo il login: " + userDetails.getUsername()));

        utente.setRefreshToken(refreshToken);
        utenteRepository.save(utente);

        log.info("Login riuscito per l'utente: {}", loginRequest.getEmail());
        return new AuthResponse(accessToken, refreshToken);
    }

    // ===== METODI ESISTENTI (nessuna modifica qui) =====

    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registrazione nuovo utente e-commerce con email: {}", registrationDTO.getEmail());
        if (utenteRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email già registrata: " + registrationDTO.getEmail());
        }
        String username = registrationDTO.getEmail().split("@")[0];
        int counter = 1;
        while (utenteRepository.findByUsername(username).isPresent()) {
            username = registrationDTO.getEmail().split("@")[0] + counter++;
        }
        Utente utente = Utente.builder()
                .username(username)
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .ruolo("USER")
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .address(registrationDTO.getAddress())
                .telephone(registrationDTO.getTelephone())
                .build();
        Utente savedUser = utenteRepository.save(utente);
        cartService.createCartForUser(savedUser);
        log.info("Utente registrato con successo. ID: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        return convertToDTO(utente);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + email));
        return convertToDTO(utente);
    }

    /**
     * Recupera un utente tramite username (per compatibilità JWT)
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato: " + username));
        return convertToDTO(utente);
    }

    /**
     * Recupera tutti gli utenti
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return utenteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Aggiorna i dati di un utente
     */
    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        log.info("Aggiornamento utente con ID: {}", id);

        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));

        // Aggiorna solo i campi forniti
        if (updateDTO.getFirstName() != null) {
            utente.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            utente.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getAddress() != null) {
            utente.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getTelephone() != null) {
            utente.setTelephone(updateDTO.getTelephone());
        }

        // Se viene fornita una nuova password, la cifriamo
        if (updateDTO.getNewPassword() != null) {
            utente.setPassword(passwordEncoder.encode(updateDTO.getNewPassword()));
        }

        Utente updatedUser = utenteRepository.save(utente);
        log.info("Utente aggiornato con successo. ID: {}", updatedUser.getId());

        return convertToDTO(updatedUser);
    }

    /**
     * Elimina un utente (soft delete - disabilita l'account)
     */
    public void deleteUser(Long id) {
        log.info("Eliminazione utente con ID: {}", id);

        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));

        // Per ora, eliminiamo fisicamente
        // In futuro si potrebbe implementare un soft delete con un campo 'isActive'
        utenteRepository.delete(utente);
        log.info("Utente eliminato con successo. ID: {}", id);
    }

    /**
     * Aggiunge un'autorità/ruolo a un utente
     */
    public UserDTO addAuthority(Long userId, String authority) {
        log.info("Aggiunta autorità {} all'utente {}", authority, userId);

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        // Per ora gestiamo un singolo ruolo, ma possiamo estendere per ruoli multipli
        if ("ROLE_ADMIN".equals(authority) || "ADMIN".equals(authority)) {
            utente.setRuolo("ADMIN");
        } else if ("ROLE_USER".equals(authority) || "USER".equals(authority)) {
            utente.setRuolo("USER");
        } else {
            utente.setRuolo(authority);
        }

        Utente updatedUser = utenteRepository.save(utente);

        return convertToDTO(updatedUser);
    }

    /**
     * Rimuove un'autorità/ruolo da un utente
     */
    public UserDTO removeAuthority(Long userId, String authority) {
        log.info("Rimozione autorità {} dall'utente {}", authority, userId);

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        // Se si rimuove il ruolo corrente, impostiamo USER come default
        if (utente.getRuolo().equals(authority) ||
                utente.getRuolo().equals("ROLE_" + authority) ||
                ("ROLE_" + utente.getRuolo()).equals(authority)) {
            utente.setRuolo("USER");
        }

        Utente updatedUser = utenteRepository.save(utente);

        return convertToDTO(updatedUser);
    }

    /**
     * Trova utenti per autorità/ruolo
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByAuthority(String authority) {
        // Poiché abbiamo un singolo ruolo, filtriamo direttamente
        return utenteRepository.findAll().stream()
                .filter(u -> u.getRuolo().equals(authority) ||
                        u.getRuolo().equals("ROLE_" + authority) ||
                        ("ROLE_" + u.getRuolo()).equals(authority))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cerca utenti per nome
     */
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByName(String name) {
        return utenteRepository.findByName(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica se un'email è disponibile
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return utenteRepository.findByEmail(email).isEmpty();
    }

    /**
     * Cambia la password di un utente
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Cambio password per utente: {}", userId);

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        // Verifica che la password corrente sia corretta
        if (!passwordEncoder.matches(currentPassword, utente.getPassword())) {
            throw new IllegalArgumentException("Password corrente non valida");
        }

        utente.setPassword(passwordEncoder.encode(newPassword));
        utenteRepository.save(utente);

        log.info("Password cambiata con successo per utente: {}", userId);
    }

    private UserDTO convertToDTO(Utente utente) {
        Set<String> authorities = new HashSet<>();
        if (utente.getRuolo() != null) {
            String role = utente.getRuolo();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            authorities.add(role);
        }
        return UserDTO.builder()
                .id(utente.getId())
                .firstName(utente.getFirstName())
                .lastName(utente.getLastName())
                .email(utente.getEmail())
                .address(utente.getAddress())
                .telephone(utente.getTelephone())
                .authorities(authorities)
                .orderCount(utente.getOrders() != null ? utente.getOrders().size() : 0)
                .hasActiveCart(utente.getCarts() != null &&
                        utente.getCarts().stream().anyMatch(Cart::isActive))
                .build();
    }
}
package com.example.project_security.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project_security.dto.UserDTO;
import com.example.project_security.dto.request.UserRegistrationDTO;
import com.example.project_security.dto.request.UserUpdateDTO;
import com.example.project_security.exception.DuplicateResourceException;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.model.User;
import com.example.project_security.repository.UserRepository;

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

    private final UtenteRepository utenteRepository;  // ← Usa UtenteRepository
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    /**
     * Registra un nuovo utente e-commerce completo
     */
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registrazione nuovo utente e-commerce con email: {}", registrationDTO.getEmail());

        // Verifica che l'email non sia già registrata
        if (utenteRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email già registrata: " + registrationDTO.getEmail());
        }

        // Crea username univoco dall'email se non fornito
        String username = registrationDTO.getEmail().split("@")[0];
        int counter = 1;
        while (utenteRepository.findByUsername(username).isPresent()) {
            username = registrationDTO.getEmail().split("@")[0] + counter++;
        }

        // Crea il nuovo utente
        Utente utente = Utente.builder()
                // Campi JWT
                .username(username)
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .ruolo("USER")
                // Campi E-commerce
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .address(registrationDTO.getAddress())
                .telephone(registrationDTO.getTelephone())
                .build();

        Utente savedUser = utenteRepository.save(utente);

        // Crea un carrello vuoto per il nuovo utente
        cartService.createCartForUser(savedUser);

        log.info("Utente registrato con successo. ID: {}", savedUser.getId());
        return convertToDTO(savedUser);
    }

    /**
     * Recupera un utente tramite ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        return convertToDTO(utente);
    }

    /**
     * Recupera un utente tramite email
     */
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
     * Verifica se un'email è disponibile
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return utenteRepository.findByEmail(email).isEmpty();
    }

    /**
     * Converte un'entità Utente in UserDTO
     */
    private UserDTO convertToDTO(Utente utente) {
        return UserDTO.builder()
                .id(utente.getId())
                .firstName(utente.getFirstName())
                .lastName(utente.getLastName())
                .email(utente.getEmail())
                .address(utente.getAddress())
                .telephone(utente.getTelephone())
                .authorities(Set.of(utente.getRuolo()))
                .orderCount(utente.getOrders() != null ? utente.getOrders().size() : 0)
                .hasActiveCart(utente.getCarts() != null && 
                               utente.getCarts().stream().anyMatch(Cart::isActive))
                .build();
    }
}
package com.example.project_security.service;

import com.example.project_security.dto.UserDTO;
import com.example.project_security.dto.UserRegistrationDTO;
import com.example.project_security.dto.UserUpdateDTO;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.exception.DuplicateResourceException;
import com.example.project_security.model.User;
import com.example.project_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service per la gestione degli utenti del sistema e-commerce.
 * Gestisce la registrazione, aggiornamento e recupero delle informazioni utente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;
    
    /**
     * Registra un nuovo utente nel sistema
     */
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registrazione nuovo utente con email: {}", registrationDTO.getEmail());
        
        // Verifica che l'email non sia già registrata
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("Email già registrata: " + registrationDTO.getEmail());
        }
        
        // Crea il nuovo utente
        User user = User.builder()
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDTO.getPassword()))
                .address(registrationDTO.getAddress())
                .telephone(registrationDTO.getTelephone())
                .authorities(Set.of("ROLE_USER")) // Ruolo di default
                .build();
        
        User savedUser = userRepository.save(user);
        
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        return convertToDTO(user);
    }
    
    /**
     * Recupera un utente tramite email
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + email));
        return convertToDTO(user);
    }
    
    /**
     * Recupera tutti gli utenti
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Aggiorna i dati di un utente
     */
    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        log.info("Aggiornamento utente con ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        
        // Aggiorna solo i campi forniti
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getAddress() != null) {
            user.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getTelephone() != null) {
            user.setTelephone(updateDTO.getTelephone());
        }
        
        // Se viene fornita una nuova password, la cifriamo
        if (updateDTO.getNewPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(updateDTO.getNewPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Utente aggiornato con successo. ID: {}", updatedUser.getId());
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * Elimina un utente (soft delete - disabilita l'account)
     */
    public void deleteUser(Long id) {
        log.info("Eliminazione utente con ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        
        // Invece di eliminare fisicamente, potremmo disabilitare l'account
        // Per ora, eliminiamo fisicamente
        userRepository.delete(user);
        log.info("Utente eliminato con successo. ID: {}", id);
    }
    
    /**
     * Aggiunge un'autorità/ruolo a un utente
     */
    public UserDTO addAuthority(Long userId, String authority) {
        log.info("Aggiunta autorità {} all'utente {}", authority, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));
        
        user.getAuthorities().add(authority);
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * Rimuove un'autorità/ruolo da un utente
     */
    public UserDTO removeAuthority(Long userId, String authority) {
        log.info("Rimozione autorità {} dall'utente {}", authority, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));
        
        user.getAuthorities().remove(authority);
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * Trova utenti per autorità
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByAuthority(String authority) {
        return userRepository.findByAuthority(authority).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Cerca utenti per nome
     */
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByName(String name) {
        return userRepository.findByName(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica se un'email è disponibile
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    /**
     * Cambia la password di un utente
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Cambio password per utente: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));
        
        // Verifica che la password corrente sia corretta
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Password corrente non valida");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password cambiata con successo per utente: {}", userId);
    }
    
    /**
     * Converte un'entità User in UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .telephone(user.getTelephone())
                .authorities(user.getAuthorities())
                .orderCount(user.getOrders() != null ? user.getOrders().size() : 0)
                .hasActiveCart(user.getCarts().stream().anyMatch(cart -> cart.isActive()))
                .build();
    }
}
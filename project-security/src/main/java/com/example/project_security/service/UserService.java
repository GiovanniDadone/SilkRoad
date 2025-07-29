package com.example.project_security.service;

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
import com.example.project_security.model.Role;
import com.example.project_security.model.Utente;
import com.example.project_security.repository.RoleRepository;
import com.example.project_security.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UtenteRepository utenteRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse loginUser(LoginRequestDTO loginRequest) {
        log.info("Tentativo di login per l'utente: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        String refreshToken = UUID.randomUUID().toString();
        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato dopo il login: " + userDetails.getUsername()));

        utente.setRefreshToken(refreshToken);
        utenteRepository.save(utente);

        return new AuthResponse(accessToken, refreshToken);
    }

    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (utenteRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email già registrata: " + registrationDTO.getEmail());
        }

        String username = registrationDTO.getEmail().split("@")[0];
        int counter = 1;
        while (utenteRepository.findByUsername(username).isPresent()) {
            username = registrationDTO.getEmail().split("@")[0] + counter++;
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Ruolo ROLE_USER non trovato"));

        Utente utente = Utente.builder()
                .username(username)
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .roles(Set.of(userRole))
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .address(registrationDTO.getAddress())
                .telephone(registrationDTO.getTelephone())
                .build();

        Utente savedUser = utenteRepository.save(utente);
        cartService.createCartForUser(savedUser);
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

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato: " + username));
        return convertToDTO(utente);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return utenteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));

        if (updateDTO.getFirstName() != null) utente.setFirstName(updateDTO.getFirstName());
        if (updateDTO.getLastName() != null) utente.setLastName(updateDTO.getLastName());
        if (updateDTO.getAddress() != null) utente.setAddress(updateDTO.getAddress());
        if (updateDTO.getEmail() != null) utente.setEmail(updateDTO.getEmail());
        if (updateDTO.getTelephone() != null) utente.setTelephone(updateDTO.getTelephone());
        if (updateDTO.getNewPassword() != null)
            utente.setPassword(passwordEncoder.encode(updateDTO.getNewPassword()));

        return convertToDTO(utenteRepository.save(utente));
    }

    public void deleteUser(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        utenteRepository.delete(utente);
    }

    public UserDTO addAuthority(Long userId, String authority) {
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        Role role = roleRepository.findByName(authority.startsWith("ROLE_") ? authority : "ROLE_" + authority)
                .orElseThrow(() -> new ResourceNotFoundException("Ruolo non trovato: " + authority));

        utente.getRoles().add(role);
        return convertToDTO(utenteRepository.save(utente));
    }

    public UserDTO removeAuthority(Long userId, String authority) {
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        String normalized = authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;
        utente.getRoles().removeIf(role -> role.getName().equals(normalized));

        return convertToDTO(utenteRepository.save(utente));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByAuthority(String authority) {
        String normalized = authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;
        return utenteRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals(normalized)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByName(String name) {
        return utenteRepository.findByName(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return utenteRepository.findByEmail(email).isEmpty();
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        if (!passwordEncoder.matches(currentPassword, utente.getPassword())) {
            throw new IllegalArgumentException("Password corrente non valida");
        }

        utente.setPassword(passwordEncoder.encode(newPassword));
        utenteRepository.save(utente);
    }

    private UserDTO convertToDTO(Utente utente) {
        Set<String> authorities = utente.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDTO.builder()
                .id(utente.getId())
                .firstName(utente.getFirstName())
                .lastName(utente.getLastName())
                .email(utente.getEmail())
                .address(utente.getAddress())
                .telephone(utente.getTelephone())
                .authorities(authorities)
                .orderCount(utente.getOrders() != null ? utente.getOrders().size() : 0)
                .hasActiveCart(utente.getCarts() != null && utente.getCarts().stream().anyMatch(Cart::isActive))
                .build();
    }
}
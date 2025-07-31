package com.example.project_security.service;

import com.example.project_security.model.Role;
import com.example.project_security.model.Utente;
import com.example.project_security.repository.UtenteRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteRepository repo;

    public CustomUserDetailsService(UtenteRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utente u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente con email " + email + " non trovato"));

        Set<GrantedAuthority> authorities = u.getRoles().stream()
                .map(Role::getName)                            
                .map(SimpleGrantedAuthority::new)             
                .collect(Collectors.toSet());

        return new User(u.getEmail(), u.getPassword(), authorities);
    }
}

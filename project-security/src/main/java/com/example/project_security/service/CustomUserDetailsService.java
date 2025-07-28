package com.example.project_security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.project_security.model.Utente;
import com.example.project_security.repository.UtenteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteRepository repo;

    public CustomUserDetailsService(UtenteRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        return User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRuolo()) // es. "USER"
                .build();
    }
}

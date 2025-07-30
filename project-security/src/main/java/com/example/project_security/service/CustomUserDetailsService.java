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
        // Correggi qui: cerca l'utente per email, perché il 'username' che ricevi
        // è l'email fornita nel login.
        Utente u = repo.findByEmail(username) // <-- CAMBIATO DA findByUsername A findByEmail
                .orElseThrow(() -> new UsernameNotFoundException("Utente con email " + username + " non trovato"));

        // Se l'email dell'utente è usata come campo username per Spring Security,
        // allora u.getEmail() sarà il valore corretto per User.builder().username().
        // u.getUsername() potrebbe essere diverso dall'email se l'hai generato
        // automaticamente come "parte_prima_dell_@"
        // Assicurati che il "username" del UserDetails sia ciò che ti aspetti come
        // identificatore unico.
        // Se la tua logica è usare l'email come username di Spring Security, allora
        // u.getEmail() è più appropriato qui.
        // Ma, se il campo 'username' nel tuo modello Utente è pensato per essere
        // l'identificatore principale
        // e lo hai generato basandoti sull'email (es. mario.rossi -> mario.rossi),
        // allora la ricerca in findByUsername(username) è giusta, ma l'input 'username'
        // del metodo loadUserByUsername dovrebbe essere il campo `username`
        // dell'Utente, non l'email.
        // Data la tua implementazione di `UserService.loginUser` che passa
        // `loginRequest.getEmail()`,
        // la modifica a `findByEmail` è la più diretta.

        return User.builder()
                .username(u.getEmail()) // E' più sicuro usare l'email qui se è quello che passi come login identifier
                .password(u.getPassword())
                .roles(u.getRuolo())
                .build();
    }
}
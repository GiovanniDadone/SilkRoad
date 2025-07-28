package com.example.project_security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.project_security.model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByUsername(String username);
}

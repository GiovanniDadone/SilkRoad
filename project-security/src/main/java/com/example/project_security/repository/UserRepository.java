package com.example.project_security.repository;

import com.example.project_security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità User.
 * Estende JpaRepository per avere i metodi base di persistenza.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Trova un utente tramite email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica se esiste un utente con una determinata email
     */
    boolean existsByEmail(String email);
    
    /**
     * Trova tutti gli utenti che contengono una specifica autorità
     */
    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a = :authority")
    List<User> findByAuthority(@Param("authority") String authority);
    
    /**
     * Trova utenti per nome o cognome (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByName(@Param("name") String name);
    
    /**
     * Trova utenti che hanno effettuato ordini
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o")
    List<User> findUsersWithOrders();
    
    /**
     * Trova utenti con carrello attivo
     */
    @Query("SELECT u FROM User u JOIN u.carts c WHERE c.isActive = true")
    List<User> findUsersWithActiveCart();
    
    /**
     * Conta gli utenti per autorità
     */
    @Query("SELECT a, COUNT(u) FROM User u JOIN u.authorities a GROUP BY a")
    List<Object[]> countUsersByAuthority();
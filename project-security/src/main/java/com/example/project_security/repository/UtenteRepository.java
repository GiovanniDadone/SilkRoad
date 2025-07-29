package com.example.project_security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project_security.model.Utente;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    
        // Metodi esistenti per JWT
        Optional<Utente> findByUsername(String username);
    
    
        boolean existsByUsername(String username);
    

    /**
     * Trova utenti che hanno effettuato ordini
     */
        @Query("SELECT DISTINCT u FROM Utente u JOIN u.orders o")
        List<Utente> findUsersWithOrders();

    /**
     * Trova utenti con carrello attivo
     */
        @Query("SELECT u FROM Utente u JOIN u.carts c WHERE c.isActive = true")
        List<Utente> findUsersWithActiveCart();

        /**
        * Trova un utente tramite email
        */
       Optional<Utente> findByEmail(String email);

       /**
        * Verifica se esiste un utente con una determinata email
        */
       boolean existsByEmail(String email);

       /**
        * Trova tutti gli utenti che contengono una specifica autorità
        */
       @Query("SELECT u FROM Utente u JOIN u.authorities a WHERE a = :authority")
       List<Utente> findByAuthority(@Param("authority") String authority);

       /**
        * Trova utenti per nome o cognome (case insensitive)
        */
       @Query("SELECT u FROM Utente u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
                     "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
       List<Utente> findByName(@Param("name") String name);

       /**
        * Trova utenti che hanno effettuato ordini
        */
       @Query("SELECT DISTINCT u FROM Utente u JOIN u.orders o")
       List<Utente> findUtentesWithOrders();

       /**
        * Trova utenti con carrello attivo
        */
       @Query("SELECT u FROM Utente u JOIN u.carts c WHERE c.isActive = true")
       List<Utente> findUtentesWithActiveCart();

       /**
        * Conta gli utenti per autorità
        */
       @Query("SELECT a, COUNT(u) FROM Utente u JOIN u.authorities a GROUP BY a")
       List<Object[]> countUtentesByAuthority();
}

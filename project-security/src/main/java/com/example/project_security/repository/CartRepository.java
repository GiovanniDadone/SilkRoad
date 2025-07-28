package com.example.project_security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project_security.model.Cart;
import com.example.project_security.model.User;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità Cart.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Trova il carrello attivo di un utente
     */
    Optional<Cart> findByUserAndIsActiveTrue(User user);
    
    /**
     * Trova il carrello attivo tramite user id
     */
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.isActive = true")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId);
    
    /**
     * Trova tutti i carrelli di un utente (attivi e non)
     */
    List<Cart> findByUserOrderByIdDesc(User user);
    
    /**
     * Trova tutti i carrelli attivi
     */
    List<Cart> findByIsActiveTrue();
    
    /**
     * Conta i carrelli attivi
     */
    long countByIsActiveTrue();
    
    /**
     * Trova carrelli con almeno un item
     */
    @Query("SELECT DISTINCT c FROM Cart c JOIN c.cartItems ci WHERE c.isActive = true")
    List<Cart> findActiveCartsWithItems();
    
    /**
     * Calcola il valore totale di tutti i carrelli attivi
     */
    @Query("SELECT SUM(ci.unitPrice * ci.quantity) FROM Cart c JOIN c.cartItems ci WHERE c.isActive = true")
    Double calculateTotalValueOfActiveCarts();
    
    /**
     * Trova carrelli abbandonati (attivi da più di X giorni senza ordine)
     */
    @Query(value = "SELECT c.* FROM carts c WHERE c.is_active = true " +
           "AND c.id IN (SELECT cart_id FROM cart_items) " +
           "AND NOT EXISTS (SELECT 1 FROM orders o WHERE o.user_id = c.user_id " +
           "AND o.order_date > DATE_SUB(NOW(), INTERVAL :days DAY))", 
           nativeQuery = true)
    List<Cart> findAbandonedCarts(@Param("days") int days);
}
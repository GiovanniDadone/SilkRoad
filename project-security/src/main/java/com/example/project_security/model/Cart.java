package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità Cart che rappresenta il carrello degli acquisti di un utente.
 * Contiene i CartItem che rappresentano i prodotti nel carrello.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carts")
@EqualsAndHashCode(exclude = {"user", "cartItems"})
@ToString(exclude = {"user", "cartItems"})
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * CAMBIATO: Ora punta a Utente invece di User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utente user;  // ← CAMBIATO da User a Utente
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CartItem> cartItems = new HashSet<>();
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    // Metodi di utilità
    
    /**
     * Aggiunge un item al carrello
     */
    public void addCartItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
    }
    
    /**
     * Rimuove un item dal carrello
     */
    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
    }
    
    /**
     * Calcola il totale del carrello
     */
  @Transient
public BigDecimal getTotalPrice() {
    return cartItems.stream()
            .map(item -> {
                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                return price.multiply(BigDecimal.valueOf(quantity));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

    /**
     * Calcola il numero totale di articoli nel carrello
     */
    @Transient
    public Integer getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    /**
     * Svuota il carrello
     */
    public void clear() {
        cartItems.clear();
    }
}
package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
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
     * Relazione molti-a-uno con User.
     * Ogni carrello appartiene a un utente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Relazione uno-a-molti con CartItem.
     * Un carrello contiene più elementi
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> cartItems = new HashSet<>();
    
    /**
     * Flag per indicare se il carrello è attivo.
     * Un utente dovrebbe avere solo un carrello attivo alla volta
     */
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
    public Double getTotalPrice() {
        return cartItems.stream()
                .mapToDouble(item -> item.getUnitPrice().doubleValue() * item.getQuantity())
                .sum();
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
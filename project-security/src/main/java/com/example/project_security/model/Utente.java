package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità Utente unificata che gestisce sia autenticazione JWT che profilo e-commerce
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "utenti")
@EqualsAndHashCode(exclude = {"carts", "orders"})
@ToString(exclude = {"carts", "orders"})
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== CAMPI JWT/AUTENTICAZIONE =====
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String ruolo;

    private String refreshToken;

    // ===== CAMPI E-COMMERCE (NUOVI) =====
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(unique = true, length = 100)
    private String email;
    
    @Column(length = 200)
    private String address;
    
    @Column(length = 20)
    private String telephone;

    // ===== RELAZIONI E-COMMERCE =====
    /**
     * Relazione uno-a-molti con Cart.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Cart> carts = new HashSet<>();
    
    /**
     * Relazione uno-a-molti con Order.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();

    // ===== METODI UTILITÀ =====
    public void addCart(Cart cart) {
        if (this.carts == null) {
            this.carts = new HashSet<>();
        }
        carts.add(cart);
        cart.setUser(this);
    }
    
    public void removeCart(Cart cart) {
        if (this.carts != null) {
            carts.remove(cart);
            cart.setUser(null);
        }
    }
    
    public void addOrder(Order order) {
        if (this.orders == null) {
            this.orders = new HashSet<>();
        }
        orders.add(order);
        order.setUser(this);
    }
    
    public void removeOrder(Order order) {
        if (this.orders != null) {
            orders.remove(order);
            order.setUser(null);
        }
    }

    /**
     * Verifica se l'utente ha un profilo e-commerce completo
     */
    @Transient
    public boolean hasCompleteProfile() {
        return firstName != null && lastName != null && 
               email != null && address != null && telephone != null;
    }
}
package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità User che rappresenta un utente del sistema e-commerce.
 * Estende la classe Utente esistente per mantenere la compatibilità con il sistema di autenticazione.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@EqualsAndHashCode(exclude = {"carts", "orders"})
@ToString(exclude = {"carts", "orders"})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(nullable = false, length = 200)
    private String address;
    
    @Column(nullable = false, length = 20)
    private String telephone;
    
    /**
     * Set di autorità/ruoli dell'utente.
     * Utilizziamo ElementCollection per memorizzare ruoli multipli
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_authorities", 
                    joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "authority")
    private Set<String> authorities = new HashSet<>();
    
    /**
     * Relazione uno-a-molti con Cart.
     * Un utente può avere più carrelli (storico carrelli)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Cart> carts = new HashSet<>();
    
    /**
     * Relazione uno-a-molti con Order.
     * Un utente può effettuare più ordini
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();
    
    // Metodi di utilità per gestire le relazioni bidirezionali
    
    public void addCart(Cart cart) {
        carts.add(cart);
        cart.setUser(this);
    }
    
    public void removeCart(Cart cart) {
        carts.remove(cart);
        cart.setUser(null);
    }
    
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
    
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }
}
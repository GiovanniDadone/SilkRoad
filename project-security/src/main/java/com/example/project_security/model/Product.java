package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità Product che rappresenta un prodotto nel catalogo e-commerce.
 * Contiene tutte le informazioni del prodotto incluse categorie e disponibilità.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
@EqualsAndHashCode(exclude = {"category", "cartItems", "orderItems"})
@ToString(exclude = {"category", "cartItems", "orderItems"})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Quantità disponibile in magazzino
     */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    /**
     * URL dell'immagine del prodotto
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    /**
     * Relazione molti-a-uno con Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    /**
     * Relazione uno-a-molti con CartItem
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<CartItem> cartItems = new HashSet<>();
    
    /**
     * Relazione uno-a-molti con OrderItem
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();
    
    /**
     * Flag per indicare se il prodotto è attivo/disponibile per la vendita
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    /**
     * SKU (Stock Keeping Unit) - codice univoco del prodotto
     */
    @Column(unique = true, length = 50)
    private String sku;
    
    // Metodi di utilità
    
    /**
     * Verifica se il prodotto è disponibile per l'acquisto
     */
    @Transient
    public boolean isAvailable() {
        return isActive && stockQuantity > 0;
    }
    
    /**
     * Verifica se c'è abbastanza stock per la quantità richiesta
     */
    public boolean hasStock(int quantity) {
        return stockQuantity >= quantity;
    }
    
    /**
     * Decrementa lo stock della quantità specificata
     */
    public void decrementStock(int quantity) {
        if (hasStock(quantity)) {
            this.stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Stock insufficiente per il prodotto: " + name);
        }
    }
    
    /**
     * Incrementa lo stock della quantità specificata
     */
    public void incrementStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entità OrderItem che rappresenta un singolo articolo in un ordine.
 * Memorizza i dettagli del prodotto al momento dell'ordine per mantenere lo storico.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_items",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"order_id", "product_id"})
       })
@EqualsAndHashCode(exclude = {"order", "product"})
@ToString(exclude = {"order", "product"})
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Quantità ordinata del prodotto
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Prezzo unitario al momento dell'ordine.
     * Memorizzato per mantenere lo storico del prezzo
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    /**
     * Relazione molti-a-uno con Order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    /**
     * Relazione molti-a-uno con Product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    /**
     * Nome del prodotto al momento dell'ordine
     * (nel caso il prodotto venga modificato o eliminato)
     */
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    /**
     * SKU del prodotto al momento dell'ordine
     */
    @Column(name = "product_sku", length = 50)
    private String productSku;
    
    /**
     * Eventuale sconto applicato su questo articolo
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    // Metodi di utilità
    
    /**
     * Calcola il subtotale per questo item
     */
    @Transient
    public BigDecimal getSubtotal() {
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return subtotal.subtract(discountAmount);
    }
    
    /**
     * Calcola il subtotale senza sconti
     */
    @Transient
    public BigDecimal getSubtotalBeforeDiscount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    /**
     * Crea un OrderItem da un CartItem
     */
    public static OrderItem fromCartItem(CartItem cartItem) {
        return OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .product(cartItem.getProduct())
                .productName(cartItem.getProduct().getName())
                .productSku(cartItem.getProduct().getSku())
                .build();
    }
}
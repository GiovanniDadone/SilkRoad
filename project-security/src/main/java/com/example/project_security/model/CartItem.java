package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entità CartItem che rappresenta un singolo articolo nel carrello.
 * Contiene il riferimento al prodotto e la quantità desiderata.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "cart_id", "product_id" })
})
@EqualsAndHashCode(exclude = { "cart", "product" })
@ToString(exclude = { "cart", "product" })
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Quantità del prodotto nel carrello
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Prezzo unitario al momento dell'aggiunta al carrello.
     * Memorizzato per mantenere lo storico del prezzo
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Relazione molti-a-uno con Cart
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * Relazione molti-a-uno con Product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Calcola il subtotale per questo item
     */
    @Transient
    public BigDecimal getSubtotal() {
        BigDecimal safeUnitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        int safeQuantity = quantity != null ? quantity : 0;
        return safeUnitPrice.multiply(BigDecimal.valueOf(safeQuantity));
    }

    /**
     * Incrementa la quantità di 1
     */
    public void incrementQuantity() {
        this.quantity++;
    }

    /**
     * Decrementa la quantità di 1 (minimo 1)
     */
    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    /**
     * Aggiorna il prezzo unitario con il prezzo corrente del prodotto
     */
    public void updatePrice() {
        if (product != null) {
            this.unitPrice = product.getPrice();
        }
    }
}
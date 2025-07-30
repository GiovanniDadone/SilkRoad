package com.example.project_security.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entità Order che rappresenta un ordine effettuato da un utente.
 * Contiene tutti i dettagli dell'ordine inclusi gli OrderItem.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
@EqualsAndHashCode(exclude = {"user", "orderItems"})
@ToString(exclude = {"user", "orderItems"})
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_date", nullable = false)
    private ZonedDateTime orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus;
    
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    /**
     * CAMBIATO: Ora punta a Utente invece di User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utente user;  // ← CAMBIATO da User a Utente
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();
    
    /**
     * Indirizzo di spedizione
     */
    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;
    
    /**
     * Indirizzo di fatturazione
     */
    @Column(name = "billing_address", length = 500)
    private String billingAddress;
    
    /**
     * Note aggiuntive sull'ordine
     */
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Metodo di pagamento utilizzato
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    /**
     * ID della transazione di pagamento
     */
    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;
    
    /**
     * Data di consegna stimata
     */
    @Column(name = "estimated_delivery_date")
    private ZonedDateTime estimatedDeliveryDate;
    
    /**
     * Data di consegna effettiva
     */
    @Column(name = "actual_delivery_date")
    private ZonedDateTime actualDeliveryDate;
    
    /**
     * Numero di tracking della spedizione
     */
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;
    
    // Metodi di utilità
    
    /**
     * Aggiunge un OrderItem all'ordine
     */
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
    
    /**
     * Rimuove un OrderItem dall'ordine
     */
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
    
    /**
     * Calcola il totale dell'ordine basandosi sugli OrderItem
     */
    public void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Verifica se l'ordine può essere cancellato
     */
    @Transient
    public boolean isCancellable() {
        return orderStatus == OrderStatus.PENDING || 
               orderStatus == OrderStatus.PAYMENT_CONFIRMED;
    }
    
    /**
     * Conta il numero totale di articoli nell'ordine
     */
    @Transient
    public Integer getTotalItems() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
}
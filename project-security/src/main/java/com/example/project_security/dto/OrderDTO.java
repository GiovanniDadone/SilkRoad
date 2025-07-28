package com.example.project_security.dto;

import com.example.project_security.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO per la rappresentazione di un ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private ZonedDateTime orderDate;
    private OrderStatus orderStatus;
    private String orderStatusDescription;
    private BigDecimal totalPrice;
    private Integer totalItems;
    private String shippingAddress;
    private String billingAddress;
    private String notes;
    private String paymentMethod;
    private String paymentTransactionId;
    private String trackingNumber;
    private ZonedDateTime estimatedDeliveryDate;
    private ZonedDateTime actualDeliveryDate;
    private List<OrderItemDTO> items;
    private boolean isCancellable;
}

/**
 * DTO per la rappresentazione di un item dell'ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal subtotal;
}

/**
 * DTO per la creazione di un ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateOrderDTO {
    
    @NotBlank(message = "L'indirizzo di spedizione è obbligatorio")
    @Size(max = 500, message = "L'indirizzo di spedizione non può superare i 500 caratteri")
    private String shippingAddress;
    
    @Size(max = 500, message = "L'indirizzo di fatturazione non può superare i 500 caratteri")
    private String billingAddress;
    
    @Size(max = 1000, message = "Le note non possono superare i 1000 caratteri")
    private String notes;
    
    @NotBlank(message = "Il metodo di pagamento è obbligatorio")
    @Pattern(regexp = "^(CREDIT_CARD|DEBIT_CARD|PAYPAL|BANK_TRANSFER)$", 
            message = "Metodo di pagamento non valido")
    private String paymentMethod;
}

/**
 * DTO per l'aggiornamento dello stato di un ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateOrderStatusDTO {
    
    @NotNull(message = "Il nuovo stato è obbligatorio")
    private OrderStatus newStatus;
    
    @Size(max = 500, message = "Le note non possono superare i 500 caratteri")
    private String notes;
}

/**
 * DTO per la ricerca ordini
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderSearchDTO {
    private Long userId;
    private OrderStatus status;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String trackingNumber;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 20;
    
    @Builder.Default
    private String sortBy = "orderDate";
    
    @Builder.Default
    private String sortDirection = "DESC";
}
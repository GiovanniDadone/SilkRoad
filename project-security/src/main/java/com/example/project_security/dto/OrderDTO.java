package com.example.project_security.dto;

import com.example.project_security.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

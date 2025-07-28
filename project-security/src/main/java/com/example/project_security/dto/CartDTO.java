package com.example.project_security.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la rappresentazione del carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private Double totalPrice;
    private boolean isActive;
}

/**
 * DTO per la rappresentazione di un item nel carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal currentProductPrice;
    private BigDecimal subtotal;
    private boolean isAvailable;
    private Integer stockQuantity;
}

/**
 * DTO per aggiungere un prodotto al carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AddToCartDTO {
    
    @NotNull(message = "L'ID del prodotto è obbligatorio")
    @Positive(message = "L'ID del prodotto deve essere positivo")
    private Long productId;
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità minima è 1")
    @Max(value = 100, message = "La quantità massima è 100")
    private Integer quantity;
}

/**
 * DTO per aggiornare la quantità di un item nel carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateCartItemDTO {
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità minima è 1")
    @Max(value = 100, message = "La quantità massima è 100")
    private Integer quantity;
}
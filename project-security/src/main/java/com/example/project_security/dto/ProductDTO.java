package com.example.project_security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO per la rappresentazione di un prodotto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String sku;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private boolean isActive;
    private boolean isAvailable;
}

/**
 * DTO per la creazione di un nuovo prodotto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateProductDTO {
    
    @NotBlank(message = "Il nome del prodotto è obbligatorio")
    @Size(min = 3, max = 100, message = "Il nome deve essere tra 3 e 100 caratteri")
    private String name;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String description;
    
    @NotNull(message = "Il prezzo è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo minimo è 0.01")
    @DecimalMax(value = "999999.99", message = "Il prezzo massimo è 999999.99")
    private BigDecimal price;
    
    @NotNull(message = "La quantità in stock è obbligatoria")
    @Min(value = 0, message = "La quantità non può essere negativa")
    private Integer stockQuantity;
    
    @NotBlank(message = "Lo SKU è obbligatorio")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Lo SKU deve contenere solo lettere maiuscole, numeri e trattini")
    @Size(max = 50, message = "Lo SKU non può superare i 50 caratteri")
    private String sku;
    
    @Size(max = 500, message = "L'URL dell'immagine non può superare i 500 caratteri")
    private String imageUrl;
    
    private Long categoryId;
}

/**
 * DTO per l'aggiornamento di un prodotto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateProductDTO {
    
    @Size(min = 3, max = 100, message = "Il nome deve essere tra 3 e 100 caratteri")
    private String name;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String description;
    
    @DecimalMin(value = "0.01", message = "Il prezzo minimo è 0.01")
    @DecimalMax(value = "999999.99", message = "Il prezzo massimo è 999999.99")
    private BigDecimal price;
    
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Lo SKU deve contenere solo lettere maiuscole, numeri e trattini")
    @Size(max = 50, message = "Lo SKU non può superare i 50 caratteri")
    private String sku;
    
    @Size(max = 500, message = "L'URL dell'immagine non può superare i 500 caratteri")
    private String imageUrl;
    
    private Long categoryId;
}

/**
 * DTO per i filtri di ricerca prodotti
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ProductFilterDTO {
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private String keyword;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 20;
    
    @Builder.Default
    private String sortBy = "name";
    
    @Builder.Default
    private String sortDirection = "ASC";
}
package com.example.project_security.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per l'aggiornamento di un prodotto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductDTO {

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

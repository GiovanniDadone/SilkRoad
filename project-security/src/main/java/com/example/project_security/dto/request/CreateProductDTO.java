package com.example.project_security.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la creazione di un nuovo prodotto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDTO {

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

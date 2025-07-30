package com.example.project_security.dto.response;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per aggiungere un prodotto al carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartDTO {

    @NotNull(message = "L'ID del prodotto è obbligatorio")
    @Positive(message = "L'ID del prodotto deve essere positivo")
    private Long productId;

    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità minima è 1")
    @Max(value = 100, message = "La quantità massima è 100")
    private Integer quantity;
}

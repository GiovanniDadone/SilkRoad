package com.example.project_security.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per aggiornare la quantità di un item nel carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemDTO {

    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità minima è 1")
    @Max(value = 100, message = "La quantità massima è 100")
    private Integer quantity;
}
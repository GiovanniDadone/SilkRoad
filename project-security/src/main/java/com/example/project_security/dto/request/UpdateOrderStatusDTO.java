package com.example.project_security.dto.request;

import com.example.project_security.model.OrderStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per l'aggiornamento dello stato di un ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusDTO {

    @NotNull(message = "Il nuovo stato è obbligatorio")
    private OrderStatus newStatus;

    @Size(max = 500, message = "Le note non possono superare i 500 caratteri")
    private String notes;
}

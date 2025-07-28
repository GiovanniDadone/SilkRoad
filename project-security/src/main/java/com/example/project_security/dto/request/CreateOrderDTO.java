package com.example.project_security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la creazione di un ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDTO {

    @NotBlank(message = "L'indirizzo di spedizione è obbligatorio")
    @Size(max = 500, message = "L'indirizzo di spedizione non può superare i 500 caratteri")
    private String shippingAddress;

    @Size(max = 500, message = "L'indirizzo di fatturazione non può superare i 500 caratteri")
    private String billingAddress;

    @Size(max = 1000, message = "Le note non possono superare i 1000 caratteri")
    private String notes;

    @NotBlank(message = "Il metodo di pagamento è obbligatorio")
    @Pattern(regexp = "^(CREDIT_CARD|DEBIT_CARD|PAYPAL|BANK_TRANSFER)$", message = "Metodo di pagamento non valido")
    private String paymentMethod;
}

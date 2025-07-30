package com.example.project_security.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per l'aggiornamento di una categoria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryDTO {

    @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
    @Pattern(regexp = "^[a-zA-Z0-9À-ú\\s-]+$", message = "Il nome può contenere solo lettere, numeri, spazi e trattini")
    private String name;

    @Size(max = 500, message = "La descrizione non può superare i 500 caratteri")
    private String description;

    @Size(max = 500, message = "L'URL dell'immagine non può superare i 500 caratteri")
    private String imageUrl;

    @Min(value = 0, message = "L'ordine di visualizzazione deve essere positivo")
    private Integer displayOrder;

    private Long parentCategoryId;

    private Boolean removeParent;
}
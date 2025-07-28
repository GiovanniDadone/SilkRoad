package com.example.project_security.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la rappresentazione di una categoria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private boolean isActive;
    private Long parentCategoryId;
    private String parentCategoryName;
    private long productCount;
    private boolean hasSubcategories;
}

/**
 * DTO per la rappresentazione gerarchica delle categorie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CategoryTreeDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private long productCount;
    private List<CategoryTreeDTO> children;
}

/**
 * DTO per la creazione di una nuova categoria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateCategoryDTO {
    
    @NotBlank(message = "Il nome della categoria è obbligatorio")
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
}

/**
 * DTO per l'aggiornamento di una categoria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateCategoryDTO {
    
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
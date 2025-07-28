package com.example.project_security.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per i filtri di ricerca prodotti
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterDTO {
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

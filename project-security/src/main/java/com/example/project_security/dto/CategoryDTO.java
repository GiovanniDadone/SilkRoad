package com.example.project_security.dto;

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

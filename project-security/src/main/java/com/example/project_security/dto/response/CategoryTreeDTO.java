package com.example.project_security.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la rappresentazione gerarchica delle categorie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private long productCount;
    private List<CategoryTreeDTO> children;
}

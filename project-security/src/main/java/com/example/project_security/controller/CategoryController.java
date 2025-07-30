package com.example.project_security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project_security.dto.CategoryDTO;
import com.example.project_security.dto.request.CreateCategoryDTO;
import com.example.project_security.dto.request.UpdateCategoryDTO;
import com.example.project_security.dto.response.CategoryTreeDTO;
import com.example.project_security.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller per la gestione delle categorie dei prodotti.
 * Supporta categorie gerarchiche e operazioni CRUD.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "API per la gestione delle categorie")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Recupera tutte le categorie attive
     */
    @GetMapping
    @Operation(summary = "Recupera tutte le categorie attive")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Recupera l'albero completo delle categorie
     */
    @GetMapping("/tree")
    @Operation(summary = "Recupera l'albero gerarchico delle categorie")
    public ResponseEntity<List<CategoryTreeDTO>> getCategoryTree() {
        List<CategoryTreeDTO> categoryTree = categoryService.getCategoryTree();
        return ResponseEntity.ok(categoryTree);
    }

    /**
     * Recupera le categorie radice (senza padre)
     */
    @GetMapping("/roots")
    @Operation(summary = "Recupera le categorie radice")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        List<CategoryDTO> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(rootCategories);
    }

    /**
     * Recupera una categoria specifica per ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Recupera una categoria specifica per ID")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Recupera una categoria per nome
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "Recupera una categoria per nome")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        CategoryDTO category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    /**
     * Recupera le sottocategorie di una categoria
     */
    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Recupera le sottocategorie di una categoria")
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long id) {
        List<CategoryDTO> subcategories = categoryService.getSubcategories(id);
        return ResponseEntity.ok(subcategories);
    }

    /**
     * Recupera il percorso completo di una categoria (breadcrumb)
     */
    @GetMapping("/{id}/path")
    @Operation(summary = "Recupera il percorso completo di una categoria")
    public ResponseEntity<List<CategoryDTO>> getCategoryPath(@PathVariable Long id) {
        List<CategoryDTO> path = categoryService.getCategoryPath(id);
        return ResponseEntity.ok(path);
    }

    /**
     * Recupera categorie con prodotti attivi
     */
    @GetMapping("/with-products")
    @Operation(summary = "Recupera solo le categorie che contengono prodotti attivi")
    public ResponseEntity<List<CategoryDTO>> getCategoriesWithProducts() {
        List<CategoryDTO> categories = categoryService.getCategoriesWithProducts();
        return ResponseEntity.ok(categories);
    }

    /**
     * Conta i prodotti per categoria
     */
    @GetMapping("/product-count")
    @Operation(summary = "Conta i prodotti attivi per ogni categoria")
    public ResponseEntity<List<Object[]>> getProductCountByCategory() {
        List<Object[]> counts = categoryService.getProductCountByCategory();
        return ResponseEntity.ok(counts);
    }

    // ===== ADMIN ENDPOINTS =====

    /**
     * Crea una nuova categoria (solo admin)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea una nuova categoria (solo admin)")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryDTO createDTO) {
        CategoryDTO newCategory = categoryService.createCategory(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    /**
     * Aggiorna una categoria (solo admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna una categoria (solo admin)")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO updateDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, updateDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Attiva/disattiva una categoria (solo admin)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Attiva/disattiva una categoria (solo admin)")
    public ResponseEntity<CategoryDTO> toggleCategoryStatus(@PathVariable Long id) {
        CategoryDTO updatedCategory = categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Elimina una categoria (solo admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina una categoria (solo admin)")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "Categoria eliminata con successo"));
    }
}
package com.example.project_security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.project_security.dto.ProductDTO;
import com.example.project_security.dto.request.CreateProductDTO;
import com.example.project_security.dto.request.UpdateProductDTO;
import com.example.project_security.dto.response.ProductFilterDTO;
import com.example.project_security.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller per la gestione dei prodotti del catalogo e-commerce.
 * Fornisce endpoint per la ricerca, visualizzazione e gestione dei prodotti.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "API per la gestione dei prodotti")
public class ProductController {

    private final ProductService productService;

    /**
     * Recupera tutti i prodotti attivi con paginazione
     */
    @GetMapping
    @Operation(summary = "Recupera tutti i prodotti attivi con paginazione")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        Page<ProductDTO> products = productService.getAllActiveProducts(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(products);
    }

    /**
     * Recupera un prodotto specifico per ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Recupera un prodotto specifico per ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Recupera un prodotto per SKU
     */
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Recupera un prodotto per SKU")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        ProductDTO product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    /**
     * Ricerca prodotti per nome
     */
    @GetMapping("/search")
    @Operation(summary = "Ricerca prodotti per nome")
    public ResponseEntity<Page<ProductDTO>> searchProductsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductDTO> products = productService.searchProductsByName(name, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Ricerca avanzata prodotti
     */
    @GetMapping("/search/advanced")
    @Operation(summary = "Ricerca avanzata prodotti con keyword")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductDTO> products = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Filtra prodotti con parametri multipli
     */
    @PostMapping("/filter")
    @Operation(summary = "Filtra prodotti con parametri multipli")
    public ResponseEntity<Page<ProductDTO>> filterProducts(@RequestBody ProductFilterDTO filterDTO) {
        Page<ProductDTO> products = productService.getProductsWithFilters(filterDTO);
        return ResponseEntity.ok(products);
    }

    /**
     * Recupera prodotti per categoria
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Recupera prodotti per categoria")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductDTO> products = productService.getProductsByCategory(categoryId, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Recupera prodotti con stock basso
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera prodotti con stock basso (solo admin)")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    /**
     * Recupera prodotti esauriti
     */
    @GetMapping("/out-of-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera prodotti esauriti (solo admin)")
    public ResponseEntity<List<ProductDTO>> getOutOfStockProducts() {
        List<ProductDTO> products = productService.getOutOfStockProducts();
        return ResponseEntity.ok(products);
    }

    // ===== ADMIN ENDPOINTS =====

    /**
     * Crea un nuovo prodotto (solo admin)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crea un nuovo prodotto (solo admin)")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductDTO createDTO) {
        ProductDTO newProduct = productService.createProduct(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    /**
     * Aggiorna un prodotto (solo admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna un prodotto (solo admin)")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDTO updateDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, updateDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Aggiorna lo stock di un prodotto (solo admin)
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna lo stock di un prodotto (solo admin)")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> stockData) {
        Integer quantity = stockData.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().body(null);
        }
        ProductDTO updatedProduct = productService.updateStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Incrementa lo stock di un prodotto (solo admin)
     */
    @PatchMapping("/{id}/stock/increment")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Incrementa lo stock di un prodotto (solo admin)")
    public ResponseEntity<ProductDTO> incrementStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> stockData) {
        Integer quantity = stockData.get("quantity");
        if (quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        ProductDTO updatedProduct = productService.incrementStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Attiva/disattiva un prodotto (solo admin)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Attiva/disattiva un prodotto (solo admin)")
    public ResponseEntity<ProductDTO> toggleProductStatus(@PathVariable Long id) {
        ProductDTO updatedProduct = productService.toggleProductStatus(id);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Elimina un prodotto (solo admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina un prodotto (solo admin)")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Prodotto eliminato con successo"));
    }
}
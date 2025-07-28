package com.example.project_security.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project_security.dto.ProductDTO;
import com.example.project_security.dto.request.CreateProductDTO;
import com.example.project_security.dto.request.UpdateProductDTO;
import com.example.project_security.dto.response.ProductFilterDTO;
import com.example.project_security.exception.DuplicateResourceException;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.model.Category;
import com.example.project_security.model.Product;
import com.example.project_security.repository.CategoryRepository;
import com.example.project_security.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service per la gestione dei prodotti del catalogo e-commerce.
 * Gestisce CRUD dei prodotti, ricerca, filtraggio e gestione stock.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Crea un nuovo prodotto
     */
    public ProductDTO createProduct(CreateProductDTO createDTO) {
        log.info("Creazione nuovo prodotto: {}", createDTO.getName());

        // Verifica unicità SKU
        if (productRepository.existsBySku(createDTO.getSku())) {
            throw new DuplicateResourceException("SKU già esistente: " + createDTO.getSku());
        }

        // Recupera la categoria se specificata
        Category category = null;
        if (createDTO.getCategoryId() != null) {
            category = categoryRepository.findById(createDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata"));
        }

        Product product = Product.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .price(createDTO.getPrice())
                .stockQuantity(createDTO.getStockQuantity())
                .sku(createDTO.getSku())
                .imageUrl(createDTO.getImageUrl())
                .category(category)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Prodotto creato con successo. ID: {}", savedProduct.getId());

        return convertToDTO(savedProduct);
    }

    /**
     * Recupera un prodotto per ID
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con ID: " + id));
        return convertToDTO(product);
    }

    /**
     * Recupera un prodotto per SKU
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con SKU: " + sku));
        return convertToDTO(product);
    }

    /**
     * Recupera tutti i prodotti attivi con paginazione
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllActiveProducts(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Ricerca prodotti per nome
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchByName(name, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Ricerca prodotti per keyword (nome o descrizione)
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchByKeyword(keyword, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Filtra prodotti per categoria
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata"));

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryAndIsActiveTrue(category, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Filtra prodotti con filtri avanzati
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsWithFilters(ProductFilterDTO filterDTO) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(filterDTO.getSortDirection()),
                filterDTO.getSortBy());
        Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);

        if (filterDTO.getCategoryId() != null) {
            return productRepository.findByCategoryWithFilters(
                    filterDTO.getCategoryId(),
                    filterDTO.getMinPrice(),
                    filterDTO.getMaxPrice(),
                    filterDTO.getInStock(),
                    pageable).map(this::convertToDTO);
        } else if (filterDTO.getMinPrice() != null || filterDTO.getMaxPrice() != null) {
            BigDecimal minPrice = filterDTO.getMinPrice() != null ? filterDTO.getMinPrice() : BigDecimal.ZERO;
            BigDecimal maxPrice = filterDTO.getMaxPrice() != null ? filterDTO.getMaxPrice() : new BigDecimal("999999");
            return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                    .map(this::convertToDTO);
        } else {
            return getAllActiveProducts(filterDTO.getPage(), filterDTO.getSize(),
                    filterDTO.getSortBy(), filterDTO.getSortDirection());
        }
    }

    /**
     * Aggiorna un prodotto
     */
    public ProductDTO updateProduct(Long id, UpdateProductDTO updateDTO) {
        log.info("Aggiornamento prodotto con ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con ID: " + id));

        // Verifica unicità SKU se viene modificato
        if (updateDTO.getSku() != null && !updateDTO.getSku().equals(product.getSku())) {
            if (productRepository.existsBySku(updateDTO.getSku())) {
                throw new DuplicateResourceException("SKU già esistente: " + updateDTO.getSku());
            }
            product.setSku(updateDTO.getSku());
        }

        // Aggiorna i campi forniti
        if (updateDTO.getName() != null) {
            product.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getPrice() != null) {
            product.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getImageUrl() != null) {
            product.setImageUrl(updateDTO.getImageUrl());
        }
        if (updateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata"));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Prodotto aggiornato con successo");

        return convertToDTO(updatedProduct);
    }

    /**
     * Aggiorna lo stock di un prodotto
     */
    public ProductDTO updateStock(Long id, int quantity) {
        log.info("Aggiornamento stock prodotto {}: {}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con ID: " + id));

        product.setStockQuantity(quantity);
        Product updatedProduct = productRepository.save(product);

        log.info("Stock aggiornato con successo");
        return convertToDTO(updatedProduct);
    }

    /**
     * Incrementa lo stock di un prodotto
     */
    public ProductDTO incrementStock(Long id, int quantity) {
        log.info("Incremento stock prodotto {}: +{}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con ID: " + id));

        product.incrementStock(quantity);
        Product updatedProduct = productRepository.save(product);

        log.info("Stock incrementato con successo");
        return convertToDTO(updatedProduct);
    }

    /**
     * Attiva/disattiva un prodotto
     */
    public ProductDTO toggleProductStatus(Long id) {
        log.info("Toggle stato prodotto: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con ID: " + id));

        product.setActive(!product.isActive());
        Product updatedProduct = productRepository.save(product);

        log.info("Stato prodotto aggiornato: {}", product.isActive() ? "attivo" : "disattivato");
        return convertToDTO(updatedProduct);
    }

    /**
     * Elimina un prodotto (soft delete - lo disattiva)
     */
    public void deleteProduct(Long id) {
        log.info("Eliminazione prodotto: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato con ID: " + id));

        product.setActive(false);
        productRepository.save(product);

        log.info("Prodotto disattivato con successo");
    }

    /**
     * Trova prodotti con stock basso
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Trova prodotti esauriti
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getOutOfStockProducts() {
        return productRepository.findByStockQuantityAndIsActiveTrue(0).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte Product entity in ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .isActive(product.isActive())
                .isAvailable(product.isAvailable())
                .build();
    }
}
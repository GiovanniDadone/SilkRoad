package com.example.project_security.repository;

import com.example.project_security.model.Category;
import com.example.project_security.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità Product.
 * Include metodi per ricerca, filtraggio e paginazione dei prodotti.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Trova un prodotto tramite SKU
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Trova tutti i prodotti attivi
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);
    List<Product> findByIsActiveTrue();
    
    /**
     * Trova prodotti per categoria
     */
    Page<Product> findByCategoryAndIsActiveTrue(Category category, Pageable pageable);
    List<Product> findByCategoryAndIsActiveTrue(Category category);
    
    /**
     * Ricerca prodotti per nome (case insensitive)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
    Page<Product> searchByName(@Param("name") String name, Pageable pageable);
    
    /**
     * Ricerca prodotti per nome o descrizione
     */
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.isActive = true")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Trova prodotti in un range di prezzo
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice, 
                                   Pageable pageable);
    
    /**
     * Trova prodotti con stock basso
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    /**
     * Trova prodotti esauriti
     */
    List<Product> findByStockQuantityAndIsActiveTrue(int stockQuantity);
    
    /**
     * Trova i prodotti più venduti
     */
    @Query("SELECT p, COUNT(oi) as salesCount FROM Product p " +
           "JOIN p.orderItems oi " +
           "GROUP BY p " +
           "ORDER BY salesCount DESC")
    List<Object[]> findBestSellingProducts(Pageable pageable);
    
    /**
     * Trova prodotti per categoria con filtri avanzati
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR :inStock = false OR p.stockQuantity > 0) " +
           "AND p.isActive = true")
    Page<Product> findByCategoryWithFilters(@Param("categoryId") Long categoryId,
                                           @Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           @Param("inStock") Boolean inStock,
                                           Pageable pageable);
    
    /**
     * Conta i prodotti per categoria
     */
    @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.category c WHERE p.isActive = true GROUP BY c")
    List<Object[]> countProductsByCategory();
    
    /**
     * Verifica se esiste un prodotto con lo stesso SKU
     */
    boolean existsBySku(String sku);
}
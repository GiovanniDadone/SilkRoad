package com.example.project_security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project_security.model.Category;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità Category.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Trova una categoria per nome
     */
    Optional<Category> findByName(String name);
    
    /**
     * Trova tutte le categorie attive
     */
    List<Category> findByIsActiveTrueOrderByDisplayOrder();
    
    /**
     * Trova tutte le categorie radice (senza categoria padre)
     */
    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL AND c.isActive = true ORDER BY c.displayOrder")
    List<Category> findRootCategories();
    
    /**
     * Trova tutte le sottocategorie di una categoria
     */
    List<Category> findByParentCategoryAndIsActiveTrueOrderByDisplayOrder(Category parentCategory);
    
    /**
     * Trova categorie con almeno un prodotto attivo
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.isActive = true AND c.isActive = true")
    List<Category> findCategoriesWithActiveProducts();
    
    /**
     * Verifica se esiste una categoria con lo stesso nome
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * Conta il numero di prodotti attivi per categoria
     */
    @Query("SELECT c.id, c.name, COUNT(p) FROM Category c LEFT JOIN c.products p " +
           "WHERE c.isActive = true AND (p IS NULL OR p.isActive = true) " +
           "GROUP BY c.id, c.name")
    List<Object[]> countActiveProductsByCategory();
    
    /**
     * Trova tutte le categorie figlie ricorsivamente
     */
    @Query(value = "WITH RECURSIVE category_tree AS (" +
           "  SELECT * FROM categories WHERE id = :categoryId " +
           "  UNION ALL " +
           "  SELECT c.* FROM categories c " +
           "  INNER JOIN category_tree ct ON c.parent_category_id = ct.id" +
           ") SELECT * FROM category_tree", nativeQuery = true)
    List<Category> findAllSubcategoriesRecursive(@Param("categoryId") Long categoryId);
    
    /**
     * Trova il percorso completo di una categoria (dalla radice alla categoria)
     */
    @Query(value = "WITH RECURSIVE category_path AS (" +
           "  SELECT * FROM categories WHERE id = :categoryId " +
           "  UNION ALL " +
           "  SELECT c.* FROM categories c " +
           "  INNER JOIN category_path cp ON c.id = cp.parent_category_id" +
           ") SELECT * FROM category_path ORDER BY id", nativeQuery = true)
    List<Category> findCategoryPath(@Param("categoryId") Long categoryId);
}
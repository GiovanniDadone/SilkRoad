package com.example.project_security.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità Category che rappresenta una categoria di prodotti.
 * Le categorie possono essere organizzate gerarchicamente.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
@EqualsAndHashCode(exclude = {"products", "parentCategory", "subCategories"})
@ToString(exclude = {"products", "parentCategory", "subCategories"})
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50, unique = true)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Relazione uno-a-molti con Product
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
    
    /**
     * Relazione auto-referenziale per categorie padre-figlio
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
    
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    private Set<Category> subCategories = new HashSet<>();
    
    /**
     * Flag per indicare se la categoria è attiva
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    /**
     * Ordine di visualizzazione
     */
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    /**
     * URL immagine della categoria
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    // Metodi di utilità
    
    /**
     * Aggiunge una sottocategoria
     */
    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }
    
    /**
     * Rimuove una sottocategoria
     */
    public void removeSubCategory(Category subCategory) {
        subCategories.remove(subCategory);
        subCategory.setParentCategory(null);
    }
    
    /**
     * Aggiunge un prodotto alla categoria
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }
    
    /**
     * Rimuove un prodotto dalla categoria
     */
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }
    
    /**
     * Verifica se è una categoria radice (senza padre)
     */
    @Transient
    public boolean isRootCategory() {
        return parentCategory == null;
    }
    
    /**
     * Conta il numero di prodotti attivi in questa categoria
     */
    @Transient
    public long getActiveProductCount() {
        return products.stream()
                .filter(Product::isActive)
                .count();
    }
}
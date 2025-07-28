package com.example.project_security.service;

import com.example.project_security.dto.CategoryDTO;
import com.example.project_security.dto.CreateCategoryDTO;
import com.example.project_security.dto.UpdateCategoryDTO;
import com.example.project_security.dto.CategoryTreeDTO;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.exception.DuplicateResourceException;
import com.example.project_security.model.Category;
import com.example.project_security.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service per la gestione delle categorie dei prodotti.
 * Supporta categorie gerarchiche con relazioni padre-figlio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Crea una nuova categoria
     */
    public CategoryDTO createCategory(CreateCategoryDTO createDTO) {
        log.info("Creazione nuova categoria: {}", createDTO.getName());
        
        // Verifica unicità del nome
        if (categoryRepository.findByName(createDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Categoria già esistente: " + createDTO.getName());
        }
        
        Category category = Category.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .imageUrl(createDTO.getImageUrl())
                .displayOrder(createDTO.getDisplayOrder() != null ? createDTO.getDisplayOrder() : 0)
                .isActive(true)
                .build();
        
        // Imposta categoria padre se specificata
        if (createDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(createDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria padre non trovata"));
            category.setParentCategory(parentCategory);
        }
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Categoria creata con successo. ID: {}", savedCategory.getId());
        
        return convertToDTO(savedCategory);
    }
    
    /**
     * Recupera una categoria per ID
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata con ID: " + id));
        return convertToDTO(category);
    }
    
    /**
     * Recupera una categoria per nome
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata: " + name));
        return convertToDTO(category);
    }
    
    /**
     * Recupera tutte le categorie attive
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrder().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Recupera tutte le categorie radice (senza padre)
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findRootCategories().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Recupera l'albero completo delle categorie
     */
    @Transactional(readOnly = true)
    public List<CategoryTreeDTO> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return rootCategories.stream()
                .map(this::buildCategoryTree)
                .collect(Collectors.toList());
    }
    
    /**
     * Recupera le sottocategorie di una categoria
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getSubcategories(Long parentId) {
        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria padre non trovata"));
        
        return categoryRepository.findByParentCategoryAndIsActiveTrueOrderByDisplayOrder(parentCategory).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Recupera il percorso completo di una categoria (breadcrumb)
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoryPath(Long categoryId) {
        return categoryRepository.findCategoryPath(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Aggiorna una categoria
     */
    public CategoryDTO updateCategory(Long id, UpdateCategoryDTO updateDTO) {
        log.info("Aggiornamento categoria con ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata con ID: " + id));
        
        // Verifica unicità del nome se viene modificato
        if (updateDTO.getName() != null && !updateDTO.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameAndIdNot(updateDTO.getName(), id)) {
                throw new DuplicateResourceException("Nome categoria già esistente: " + updateDTO.getName());
            }
            category.setName(updateDTO.getName());
        }
        
        // Aggiorna altri campi se forniti
        if (updateDTO.getDescription() != null) {
            category.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getImageUrl() != null) {
            category.setImageUrl(updateDTO.getImageUrl());
        }
        if (updateDTO.getDisplayOrder() != null) {
            category.setDisplayOrder(updateDTO.getDisplayOrder());
        }
        
        // Aggiorna categoria padre se specificata
        if (updateDTO.getParentCategoryId() != null) {
            if (updateDTO.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("Una categoria non può essere padre di se stessa");
            }
            
            // Verifica che non si crei un ciclo
            if (wouldCreateCycle(id, updateDTO.getParentCategoryId())) {
                throw new IllegalArgumentException("L'operazione creerebbe un ciclo nelle categorie");
            }
            
            Category parentCategory = categoryRepository.findById(updateDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria padre non trovata"));
            category.setParentCategory(parentCategory);
        } else if (updateDTO.getRemoveParent() != null && updateDTO.getRemoveParent()) {
            category.setParentCategory(null);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Categoria aggiornata con successo");
        
        return convertToDTO(updatedCategory);
    }
    
    /**
     * Attiva/disattiva una categoria
     */
    public CategoryDTO toggleCategoryStatus(Long id) {
        log.info("Toggle stato categoria: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata con ID: " + id));
        
        category.setActive(!category.isActive());
        
        // Se disattiviamo una categoria, disattiviamo anche le sottocategorie
        if (!category.isActive()) {
            deactivateSubcategories(category);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Stato categoria aggiornato: {}", category.isActive() ? "attiva" : "disattivata");
        
        return convertToDTO(updatedCategory);
    }
    
    /**
     * Elimina una categoria (soft delete)
     */
    public void deleteCategory(Long id) {
        log.info("Eliminazione categoria: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria non trovata con ID: " + id));
        
        // Verifica che non ci siano prodotti associati
        if (category.getActiveProductCount() > 0) {
            throw new IllegalStateException("Impossibile eliminare la categoria: contiene prodotti attivi");
        }
        
        // Verifica che non ci siano sottocategorie
        if (!category.getSubCategories().isEmpty()) {
            throw new IllegalStateException("Impossibile eliminare la categoria: contiene sottocategorie");
        }
        
        category.setActive(false);
        categoryRepository.save(category);
        
        log.info("Categoria disattivata con successo");
    }
    
    /**
     * Trova categorie con prodotti attivi
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesWithProducts() {
        return categoryRepository.findCategoriesWithActiveProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Conta i prodotti per categoria
     */
    @Transactional(readOnly = true)
    public List<Object[]> getProductCountByCategory() {
        return categoryRepository.countActiveProductsByCategory();
    }
    
    /**
     * Verifica se l'impostazione di una categoria padre creerebbe un ciclo
     */
    private boolean wouldCreateCycle(Long categoryId, Long potentialParentId) {
        if (categoryId.equals(potentialParentId)) {
            return true;
        }
        
        List<Category> descendants = categoryRepository.findAllSubcategoriesRecursive(categoryId);
        return descendants.stream().anyMatch(cat -> cat.getId().equals(potentialParentId));
    }
    
    /**
     * Disattiva ricorsivamente tutte le sottocategorie
     */
    private void deactivateSubcategories(Category category) {
        for (Category subCategory : category.getSubCategories()) {
            subCategory.setActive(false);
            categoryRepository.save(subCategory);
            deactivateSubcategories(subCategory);
        }
    }
    
    /**
     * Costruisce l'albero delle categorie ricorsivamente
     */
    private CategoryTreeDTO buildCategoryTree(Category category) {
        List<CategoryTreeDTO> children = category.getSubCategories().stream()
                .filter(Category::isActive)
                .map(this::buildCategoryTree)
                .collect(Collectors.toList());
        
        return CategoryTreeDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .productCount(category.getActiveProductCount())
                .children(children)
                .build();
    }
    
    /**
     * Converte Category entity in CategoryDTO
     */
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.isActive())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .productCount(category.getActiveProductCount())
                .hasSubcategories(!category.getSubCategories().isEmpty())
                .build();
    }
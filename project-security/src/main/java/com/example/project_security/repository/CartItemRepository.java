package com.example.project_security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project_security.model.Cart;
import com.example.project_security.model.CartItem;
import com.example.project_security.model.Product;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità CartItem.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Trova un CartItem specifico per carrello e prodotto
     */
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    
    /**
     * Trova tutti gli items di un carrello
     */
    List<CartItem> findByCart(Cart cart);
    
    /**
     * Trova tutti gli items di un carrello ordinati per id
     */
    List<CartItem> findByCartOrderByIdAsc(Cart cart);
    
    /**
     * Elimina tutti gli items di un carrello
     */
    void deleteByCart(Cart cart);
    
    /**
     * Conta gli items in un carrello
     */
    long countByCart(Cart cart);
    
    /**
     * Trova i prodotti più frequentemente aggiunti ai carrelli
     */
    @Query("SELECT ci.product, SUM(ci.quantity) as totalQuantity " +
           "FROM CartItem ci " +
           "GROUP BY ci.product " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findMostAddedProducts();
    
    /**
     * Calcola il valore totale degli items in un carrello
     */
    @Query("SELECT SUM(ci.unitPrice * ci.quantity) FROM CartItem ci WHERE ci.cart = :cart")
    Double calculateCartTotal(@Param("cart") Cart cart);
    
    /**
     * Trova items con prodotti non più disponibili
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.product.isActive = false OR ci.product.stockQuantity = 0")
    List<CartItem> findItemsWithUnavailableProducts();
    
    /**
     * Verifica se un prodotto è presente in qualche carrello attivo
     */
    @Query("SELECT COUNT(ci) > 0 FROM CartItem ci WHERE ci.product = :product AND ci.cart.isActive = true")
    boolean isProductInActiveCarts(@Param("product") Product product);
}
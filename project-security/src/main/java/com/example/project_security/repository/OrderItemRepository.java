package com.example.project_security.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project_security.model.Order;
import com.example.project_security.model.OrderItem;
import com.example.project_security.model.Product;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità OrderItem.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Trova tutti gli items di un ordine
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * Trova tutti gli items venduti di un prodotto
     */
    List<OrderItem> findByProduct(Product product);
    
    /**
     * Conta quante volte è stato venduto un prodotto
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product = :product")
    Long countProductSales(@Param("product") Product product);
    
    /**
     * Calcola la quantità totale venduta di un prodotto
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product = :product")
    Long calculateTotalQuantitySold(@Param("product") Product product);
    
    /**
     * Trova i prodotti più venduti in un periodo
     */
    @Query("SELECT oi.product, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "AND o.orderStatus NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY oi.product " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts(@Param("startDate") ZonedDateTime startDate,
                                         @Param("endDate") ZonedDateTime endDate);
    
    /**
     * Calcola il ricavo totale per prodotto
     */
    @Query("SELECT oi.product, SUM(oi.unitPrice * oi.quantity - oi.discountAmount) as totalRevenue " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.orderStatus NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY oi.product " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> calculateRevenueByProduct();
    
    /**
     * Trova gli items con sconti applicati
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.discountAmount > 0")
    List<OrderItem> findDiscountedItems();
    
    /**
     * Report vendite per categoria
     */
    @Query("SELECT p.category.name, SUM(oi.quantity), SUM(oi.unitPrice * oi.quantity) " +
           "FROM OrderItem oi " +
           "JOIN oi.product p " +
           "JOIN oi.order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "AND o.orderStatus NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY p.category.name")
    List<Object[]> generateSalesByCategoryReport(@Param("startDate") ZonedDateTime startDate,
                                                 @Param("endDate") ZonedDateTime endDate);
}
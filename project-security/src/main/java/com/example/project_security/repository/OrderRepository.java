package com.example.project_security.repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project_security.model.Order;
import com.example.project_security.model.OrderStatus;
import com.example.project_security.model.User;

/**
 * Repository per la gestione delle operazioni CRUD sull'entità Order.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Trova tutti gli ordini di un utente
     */
    Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    /**
     * Trova ordini per stato
     */
    List<Order> findByOrderStatus(OrderStatus status);
    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
    
    /**
     * Trova ordini di un utente per stato
     */
    List<Order> findByUserAndOrderStatus(User user, OrderStatus status);
    
    /**
     * Trova ordini in un range di date
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findOrdersInDateRange(@Param("startDate") ZonedDateTime startDate, 
                                     @Param("endDate") ZonedDateTime endDate);
    
    /**
     * Trova l'ultimo ordine di un utente
     */
    Optional<Order> findFirstByUserOrderByOrderDateDesc(User user);
    
    /**
     * Calcola il totale degli ordini per un utente
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user = :user AND o.orderStatus NOT IN ('CANCELLED', 'REFUNDED')")
    BigDecimal calculateUserOrdersTotal(@Param("user") User user);
    
    /**
     * Conta gli ordini per stato
     */
    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o GROUP BY o.orderStatus")
    List<Object[]> countOrdersByStatus();
    
    /**
     * Trova ordini da spedire
     */
    List<Order> findByOrderStatusOrderByOrderDateAsc(OrderStatus status);
    
    /**
     * Ricerca ordini per numero di tracking
     */
    Optional<Order> findByTrackingNumber(String trackingNumber);
    
    /**
     * Trova ordini con totale superiore a un valore
     */
    @Query("SELECT o FROM Order o WHERE o.totalPrice >= :amount ORDER BY o.totalPrice DESC")
    List<Order> findHighValueOrders(@Param("amount") BigDecimal amount);
    
    /**
     * Report vendite per periodo
     */
    @Query("SELECT DATE(o.orderDate), COUNT(o), SUM(o.totalPrice) " +
           "FROM Order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "AND o.orderStatus NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY DATE(o.orderDate) " +
           "ORDER BY DATE(o.orderDate)")
    List<Object[]> generateSalesReport(@Param("startDate") ZonedDateTime startDate,
                                      @Param("endDate") ZonedDateTime endDate);
    
    /**
     * Trova ordini che necessitano di essere processati
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'PAYMENT_CONFIRMED' ORDER BY o.orderDate ASC")
    List<Order> findOrdersToProcess();
    
    /**
     * Conta ordini per utente
     */
    @Query("SELECT o.user.id, o.user.email, COUNT(o) FROM Order o GROUP BY o.user.id, o.user.email")
    List<Object[]> countOrdersByUser();
}
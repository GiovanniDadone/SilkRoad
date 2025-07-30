package com.example.project_security.controller;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.project_security.dto.OrderDTO;
import com.example.project_security.dto.UserDTO;
import com.example.project_security.dto.request.CreateOrderDTO;
import com.example.project_security.dto.request.UpdateOrderStatusDTO;
import com.example.project_security.model.OrderStatus;
import com.example.project_security.service.OrderService;
import com.example.project_security.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller per la gestione degli ordini.
 * Gestisce la creazione ordini, tracciamento e operazioni amministrative.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Order Management", description = "API per la gestione degli ordini")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Crea un nuovo ordine dal carrello corrente
     */
    @PostMapping
    @Operation(summary = "Crea un nuovo ordine dal carrello attivo")
    public ResponseEntity<OrderDTO> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = getUserId(authentication);
        OrderDTO newOrder = orderService.createOrderFromCart(userId, createOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * Recupera tutti gli ordini dell'utente corrente
     */
    @GetMapping("/my-orders")
    @Operation(summary = "Recupera tutti gli ordini dell'utente autenticato")
    public ResponseEntity<Page<OrderDTO>> getMyOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(authentication);
        Page<OrderDTO> orders = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(orders);
    }

    /**
     * Recupera un ordine specifico dell'utente corrente
     */
    @GetMapping("/my-orders/{orderId}")
    @Operation(summary = "Recupera un ordine specifico dell'utente autenticato")
    public ResponseEntity<OrderDTO> getMyOrder(
            Authentication authentication,
            @PathVariable Long orderId) {
        Long userId = getUserId(authentication);
        OrderDTO order = orderService.getOrderByIdForUser(orderId, userId);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancella un ordine dell'utente corrente (se possibile)
     */
    @PostMapping("/my-orders/{orderId}/cancel")
    @Operation(summary = "Cancella un ordine dell'utente (se in stato cancellabile)")
    public ResponseEntity<OrderDTO> cancelMyOrder(
            Authentication authentication,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> cancelData) {
        Long userId = getUserId(authentication);
        // Verifica che l'ordine appartenga all'utente
        orderService.getOrderByIdForUser(orderId, userId);

        String reason = cancelData.getOrDefault("reason", "Cancellato dall'utente");
        OrderDTO cancelledOrder = orderService.cancelOrder(orderId, reason);
        return ResponseEntity.ok(cancelledOrder);
    }

    /**
     * Recupera il totale speso dall'utente corrente
     */
    @GetMapping("/my-orders/total")
    @Operation(summary = "Calcola il totale speso dall'utente")
    public ResponseEntity<Map<String, BigDecimal>> getMyOrdersTotal(Authentication authentication) {
        Long userId = getUserId(authentication);
        BigDecimal total = orderService.calculateUserOrdersTotal(userId);
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Traccia un ordine tramite numero di tracking
     */
    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Traccia un ordine tramite numero di tracking")
    public ResponseEntity<OrderDTO> trackOrder(@PathVariable String trackingNumber) {
        OrderDTO order = orderService.getOrderByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(order);
    }

    // ===== ADMIN ENDPOINTS =====

    /**
     * Recupera tutti gli ordini (solo admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera tutti gli ordini con paginazione (solo admin)")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Page<OrderDTO> orders = orderService.getAllOrders(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(orders);
    }

    /**
     * Recupera un ordine specifico (solo admin)
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera un ordine specifico per ID (solo admin)")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Recupera ordini per stato (solo admin)
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera ordini per stato (solo admin)")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<OrderDTO> orders = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(orders);
    }

    /**
     * Aggiorna lo stato di un ordine (solo admin)
     */
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aggiorna lo stato di un ordine (solo admin)")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusDTO updateStatusDTO) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(
                orderId,
                updateStatusDTO.getNewStatus(),
                updateStatusDTO.getNotes());
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Recupera ordini da processare (solo admin)
     */
    @GetMapping("/to-process")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera ordini in attesa di elaborazione (solo admin)")
    public ResponseEntity<List<OrderDTO>> getOrdersToProcess() {
        List<OrderDTO> orders = orderService.getOrdersToProcess();
        return ResponseEntity.ok(orders);
    }

    /**
     * Recupera ordini da spedire (solo admin)
     */
    @GetMapping("/to-ship")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recupera ordini pronti per la spedizione (solo admin)")
    public ResponseEntity<List<OrderDTO>> getOrdersToShip() {
        List<OrderDTO> orders = orderService.getOrdersToShip();
        return ResponseEntity.ok(orders);
    }

    /**
     * Genera report vendite per periodo (solo admin)
     */
    @GetMapping("/reports/sales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Genera report vendite per periodo (solo admin)")
    public ResponseEntity<List<Object[]>> generateSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        List<Object[]> report = orderService.generateSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    /**
     * Metodo helper per ottenere l'ID utente dall'autenticazione
     */
    private Long getUserId(Authentication authentication) {
        String email = authentication.getName();
        UserDTO user = userService.getUserByEmail(email);
        return user.getId();
    }
}
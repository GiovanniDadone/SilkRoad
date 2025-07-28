package com.example.project_security.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project_security.dto.OrderDTO;
import com.example.project_security.dto.OrderItemDTO;
import com.example.project_security.dto.request.CreateOrderDTO;
import com.example.project_security.exception.InsufficientStockException;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.model.Cart;
import com.example.project_security.model.CartItem;
import com.example.project_security.model.Order;
import com.example.project_security.model.OrderItem;
import com.example.project_security.model.OrderStatus;
import com.example.project_security.model.Product;
import com.example.project_security.model.User;
import com.example.project_security.repository.CartRepository;
import com.example.project_security.repository.OrderItemRepository;
import com.example.project_security.repository.OrderRepository;
import com.example.project_security.repository.ProductRepository;
import com.example.project_security.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service per la gestione degli ordini.
 * Gestisce la creazione ordini dal carrello, aggiornamento stati e tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    // private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    /**
     * Crea un nuovo ordine dal carrello attivo dell'utente
     */
    public OrderDTO createOrderFromCart(Long userId, CreateOrderDTO createOrderDTO) {
        log.info("Creazione ordine per utente: {}", userId);

        // Recupera l'utente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        // Recupera il carrello attivo
        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrello attivo non trovato"));

        // Verifica che il carrello non sia vuoto
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Impossibile creare un ordine da un carrello vuoto");
        }

        // Valida il carrello (verifica disponibilità prodotti)
        cartService.validateCart(userId);

        // Crea l'ordine
        Order order = Order.builder()
                .user(user)
                .orderDate(ZonedDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .shippingAddress(createOrderDTO.getShippingAddress() != null ? createOrderDTO.getShippingAddress()
                        : user.getAddress())
                .billingAddress(createOrderDTO.getBillingAddress() != null ? createOrderDTO.getBillingAddress()
                        : createOrderDTO.getShippingAddress())
                .notes(createOrderDTO.getNotes())
                .paymentMethod(createOrderDTO.getPaymentMethod())
                .build();

        // Crea gli OrderItem dal carrello
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Verifica ancora una volta la disponibilità
            if (!product.hasStock(cartItem.getQuantity())) {
                throw new InsufficientStockException("Stock insufficiente per: " + product.getName());
            }

            // Crea OrderItem
            OrderItem orderItem = OrderItem.fromCartItem(cartItem);
            order.addOrderItem(orderItem);

            // Decrementa lo stock
            product.decrementStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        // Calcola il totale
        order.calculateTotalPrice();

        // Salva l'ordine
        Order savedOrder = orderRepository.save(order);

        // Svuota il carrello
        cartService.clearCart(userId);

        // Disattiva il carrello e creane uno nuovo
        cart.setActive(false);
        cartRepository.save(cart);
        cartService.createCartForUser(user);

        log.info("Ordine creato con successo. ID: {}", savedOrder.getId());

        return convertToDTO(savedOrder);
    }

    /**
     * Recupera un ordine per ID
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con ID: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Recupera un ordine per ID verificando che appartenga all'utente
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderByIdForUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con ID: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Ordine non appartiene all'utente");
        }

        return convertToDTO(order);
    }

    /**
     * Recupera tutti gli ordini di un utente con paginazione
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByUserOrderByOrderDateDesc(user, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Recupera tutti gli ordini con paginazione (admin)
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return orderRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Recupera ordini per stato
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").ascending());
        return orderRepository.findByOrderStatus(status, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Aggiorna lo stato di un ordine
     */
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus, String notes) {
        log.info("Aggiornamento stato ordine {} a {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con ID: " + orderId));

        OrderStatus currentStatus = order.getOrderStatus();

        // Verifica che la transizione di stato sia valida
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Transizione non valida da %s a %s", currentStatus, newStatus));
        }

        order.setOrderStatus(newStatus);

        // Azioni specifiche per stato
        switch (newStatus) {
            case PAYMENT_CONFIRMED:
                order.setPaymentTransactionId(UUID.randomUUID().toString());
                break;
            case SHIPPED:
                order.setTrackingNumber(generateTrackingNumber());
                order.setEstimatedDeliveryDate(ZonedDateTime.now().plusDays(3));
                break;
            case DELIVERED:
                order.setActualDeliveryDate(ZonedDateTime.now());
                break;
            case CANCELLED:
            case REFUNDED:
                // Ripristina lo stock dei prodotti
                restoreProductStock(order);
                break;
        }

        // Aggiungi note se fornite
        if (notes != null && !notes.isEmpty()) {
            String existingNotes = order.getNotes() != null ? order.getNotes() + "\n" : "";
            order.setNotes(existingNotes + String.format("[%s] %s: %s",
                    ZonedDateTime.now(), newStatus, notes));
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Stato ordine aggiornato con successo");

        return convertToDTO(updatedOrder);
    }

    /**
     * Cancella un ordine (se possibile)
     */
    public OrderDTO cancelOrder(Long orderId, String reason) {
        log.info("Cancellazione ordine: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con ID: " + orderId));

        if (!order.isCancellable()) {
            throw new IllegalStateException("L'ordine non può essere cancellato nello stato attuale");
        }

        return updateOrderStatus(orderId, OrderStatus.CANCELLED, "Motivo cancellazione: " + reason);
    }

    /**
     * Recupera ordini da processare
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersToProcess() {
        return orderRepository.findOrdersToProcess().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera ordini da spedire
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersToShip() {
        return orderRepository.findByOrderStatusOrderByOrderDateAsc(OrderStatus.PROCESSING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cerca ordine per numero di tracking
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato con tracking: " + trackingNumber));
        return convertToDTO(order);
    }

    /**
     * Calcola il totale degli ordini di un utente
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateUserOrdersTotal(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        BigDecimal total = orderRepository.calculateUserOrdersTotal(user);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Genera report vendite per periodo
     */
    @Transactional(readOnly = true)
    public List<Object[]> generateSalesReport(ZonedDateTime startDate, ZonedDateTime endDate) {
        return orderRepository.generateSalesReport(startDate, endDate);
    }

    /**
     * Ripristina lo stock dei prodotti di un ordine
     */
    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.incrementStock(item.getQuantity());
            productRepository.save(product);
        }
    }

    /**
     * Genera un numero di tracking
     */
    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Converte Order entity in OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> items = order.getOrderItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderStatusDescription(order.getOrderStatus().getDescription())
                .totalPrice(order.getTotalPrice())
                .totalItems(order.getTotalItems())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .notes(order.getNotes())
                .paymentMethod(order.getPaymentMethod())
                .paymentTransactionId(order.getPaymentTransactionId())
                .trackingNumber(order.getTrackingNumber())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .actualDeliveryDate(order.getActualDeliveryDate())
                .items(items)
                .isCancellable(order.isCancellable())
                .build();
    }

    /**
     * Converte OrderItem entity in OrderItemDTO
     */
    private OrderItemDTO convertOrderItemToDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountAmount(item.getDiscountAmount())
                .subtotal(item.getSubtotal())
                .build();
    }
}
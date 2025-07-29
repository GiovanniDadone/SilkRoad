package com.example.project_security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project_security.dto.CartDTO;
import com.example.project_security.dto.CartItemDTO;
import com.example.project_security.dto.request.UpdateCartItemDTO;
import com.example.project_security.dto.response.AddToCartDTO;
import com.example.project_security.exception.InsufficientStockException;
import com.example.project_security.exception.ResourceNotFoundException;
import com.example.project_security.model.Cart;
import com.example.project_security.model.CartItem;
import com.example.project_security.model.Product;
import com.example.project_security.model.Utente;
import com.example.project_security.repository.CartItemRepository;
import com.example.project_security.repository.CartRepository;
import com.example.project_security.repository.ProductRepository;
import com.example.project_security.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service per la gestione del carrello della spesa.
 * Gestisce l'aggiunta, rimozione e aggiornamento degli articoli nel carrello.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UtenteRepository userRepository;

    /**
     * Crea un nuovo carrello per un utente
     */
    public Cart createCartForUser(Utente user) {
        //log.info("Creazione nuovo carrello per utente: {}" , user.getEmail());

        // Disattiva eventuali carrelli attivi precedenti
        cartRepository.findByUserAndIsActiveTrue(user).ifPresent(cart -> {
            cart.setActive(false);
            cartRepository.save(cart);
        });

        Cart newCart = Cart.builder()
                .user(user)
                .isActive(true)
                .build();

        return cartRepository.save(newCart);
    }

    /**
     * Recupera il carrello attivo di un utente
     */
    @Transactional(readOnly = true)
    public CartDTO getActiveCartByUserId(Long userId) {
        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseGet(() -> {
                    // Se non esiste un carrello attivo, ne creiamo uno
                    Utente user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));
                    return createCartForUser(user);
                });

        return convertToDTO(cart);
    }

    /**
     * Aggiunge un prodotto al carrello
     */
    public CartDTO addToCart(Long userId, AddToCartDTO addToCartDTO) {
        log.info("Aggiunta prodotto {} al carrello dell'utente {}", addToCartDTO.getProductId(), userId);

        // Recupera il carrello attivo
        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseGet(() -> {
                    Utente user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));
                    return createCartForUser(user);
                });

        // Recupera il prodotto
        Product product = productRepository.findById(addToCartDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Prodotto non trovato"));

        // Verifica disponibilità
        if (!product.isAvailable()) {
            throw new IllegalStateException("Prodotto non disponibile: " + product.getName());
        }

        // Verifica stock
        if (!product.hasStock(addToCartDTO.getQuantity())) {
            throw new InsufficientStockException("Stock insufficiente per il prodotto: " + product.getName());
        }

        // Verifica se il prodotto è già nel carrello
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {
            // Aggiorna la quantità
            int newQuantity = cartItem.getQuantity() + addToCartDTO.getQuantity();
            if (!product.hasStock(newQuantity)) {
                throw new InsufficientStockException("Stock insufficiente per la quantità richiesta");
            }
            cartItem.setQuantity(newQuantity);
            cartItem.updatePrice(); // Aggiorna il prezzo al prezzo corrente
        } else {
            // Crea un nuovo CartItem
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(addToCartDTO.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            cart.addCartItem(cartItem);
        }

        Cart savedCart = cartRepository.save(cart);
        log.info("Prodotto aggiunto al carrello con successo");

        return convertToDTO(savedCart);
    }

    /**
     * Aggiorna la quantità di un item nel carrello
     */
    public CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemDTO updateDTO) {
        log.info("Aggiornamento quantità item {} nel carrello", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item non trovato nel carrello"));

        // Verifica che l'item appartenga al carrello dell'utente
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Item non appartiene al carrello dell'utente");
        }

        // Verifica stock per la nuova quantità
        if (!cartItem.getProduct().hasStock(updateDTO.getQuantity())) {
            throw new InsufficientStockException("Stock insufficiente per la quantità richiesta");
        }

        cartItem.setQuantity(updateDTO.getQuantity());
        cartItem.updatePrice(); // Aggiorna il prezzo al prezzo corrente

        cartItemRepository.save(cartItem);
        log.info("Quantità aggiornata con successo");

        return convertToDTO(cartItem.getCart());
    }

    /**
     * Rimuove un item dal carrello
     */
    public CartDTO removeFromCart(Long userId, Long cartItemId) {
        log.info("Rimozione item {} dal carrello", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item non trovato nel carrello"));

        // Verifica che l'item appartenga al carrello dell'utente
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Item non appartiene al carrello dell'utente");
        }

        Cart cart = cartItem.getCart();
        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);

        log.info("Item rimosso dal carrello con successo");

        return convertToDTO(cart);
    }

    /**
     * Svuota completamente il carrello
     */
    public void clearCart(Long userId) {
        log.info("Svuotamento carrello per utente {}", userId);

        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrello attivo non trovato"));

        cartItemRepository.deleteByCart(cart);
        cart.clear();

        log.info("Carrello svuotato con successo");
    }

    /**
     * Verifica e aggiorna la disponibilità degli item nel carrello
     */
    public CartDTO validateCart(Long userId) {
        log.info("Validazione carrello per utente {}", userId);

        Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrello attivo non trovato"));

        List<CartItem> itemsToRemove = cart.getCartItems().stream()
                .filter(item -> {
                    Product product = item.getProduct();
                    // Rimuovi se il prodotto non è più disponibile
                    if (!product.isAvailable()) {
                        log.warn("Prodotto {} non più disponibile, rimosso dal carrello", product.getName());
                        return true;
                    }
                    // Aggiusta la quantità se lo stock è insufficiente
                    if (!product.hasStock(item.getQuantity())) {
                        if (product.getStockQuantity() > 0) {
                            item.setQuantity(product.getStockQuantity());
                            log.warn("Quantità ridotta a {} per prodotto {}", product.getStockQuantity(),
                                    product.getName());
                        } else {
                            log.warn("Prodotto {} esaurito, rimosso dal carrello", product.getName());
                            return true;
                        }
                    }
                    // Aggiorna il prezzo
                    item.setUnitPrice(product.getPrice());
                    return false;
                })
                .collect(Collectors.toList());

        itemsToRemove.forEach(cart::removeCartItem);
        Cart savedCart = cartRepository.save(cart);

        log.info("Carrello validato con successo");
        return convertToDTO(savedCart);
    }

    /**
     * Conta gli item nel carrello
     */
    @Transactional(readOnly = true)
    public long countItemsInCart(Long userId) {
        return cartRepository.findActiveCartByUserId(userId)
                .map(cart -> cartItemRepository.countByCart(cart))
                .orElse(0L);
    }

    /**
     * Converte Cart entity in CartDTO
     */
    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getCartItems().stream()
                .map(this::convertCartItemToDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(cart.getTotalItems())
                .totalPrice(cart.getTotalPrice())
                .isActive(cart.isActive())
                .build();
    }

    /**
     * Converte CartItem entity in CartItemDTO
     */
    private CartItemDTO convertCartItemToDTO(CartItem item) {
        Product product = item.getProduct();
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSku(product.getSku())
                .productImageUrl(product.getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .currentProductPrice(product.getPrice())
                .subtotal(item.getSubtotal())
                .isAvailable(product.isAvailable())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
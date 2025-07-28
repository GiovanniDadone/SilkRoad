package com.example.project_security.controller;

import com.example.project_security.dto.*;
import com.example.project_security.service.CartService;
import com.example.project_security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller per la gestione del carrello della spesa.
 * Gestisce l'aggiunta, rimozione e aggiornamento degli articoli nel carrello.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Shopping Cart", description = "API per la gestione del carrello")
public class CartController {
    
    private final CartService cartService;
    private final UserService userService;
    
    /**
     * Recupera il carrello attivo dell'utente corrente
     */
    @GetMapping
    @Operation(summary = "Recupera il carrello attivo dell'utente")
    public ResponseEntity<CartDTO> getCurrentCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        CartDTO cart = cartService.getActiveCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * Aggiunge un prodotto al carrello
     */
    @PostMapping("/items")
    @Operation(summary = "Aggiunge un prodotto al carrello")
    public ResponseEntity<CartDTO> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartDTO addToCartDTO) {
        Long userId = getUserId(authentication);
        CartDTO updatedCart = cartService.addToCart(userId, addToCartDTO);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Aggiorna la quantità di un item nel carrello
     */
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Aggiorna la quantità di un item nel carrello")
    public ResponseEntity<CartDTO> updateCartItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemDTO updateDTO) {
        Long userId = getUserId(authentication);
        CartDTO updatedCart = cartService.updateCartItem(userId, itemId, updateDTO);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Rimuove un item dal carrello
     */
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Rimuove un item dal carrello")
    public ResponseEntity<CartDTO> removeFromCart(
            Authentication authentication,
            @PathVariable Long itemId) {
        Long userId = getUserId(authentication);
        CartDTO updatedCart = cartService.removeFromCart(userId, itemId);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Svuota completamente il carrello
     */
    @DeleteMapping("/clear")
    @Operation(summary = "Svuota completamente il carrello")
    public ResponseEntity<Map<String, String>> clearCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "Carrello svuotato con successo"));
    }
    
    /**
     * Valida il carrello verificando disponibilità prodotti e prezzi
     */
    @PostMapping("/validate")
    @Operation(summary = "Valida il carrello verificando disponibilità e prezzi")
    public ResponseEntity<CartDTO> validateCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        CartDTO validatedCart = cartService.validateCart(userId);
        return ResponseEntity.ok(validatedCart);
    }
    
    /**
     * Conta gli item nel carrello
     */
    @GetMapping("/count")
    @Operation(summary = "Conta il numero di item nel carrello")
    public ResponseEntity<Map<String, Long>> countCartItems(Authentication authentication) {
        Long userId = getUserId(authentication);
        long count = cartService.countItemsInCart(userId);
        return ResponseEntity.ok(Map.of("count", count));
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
package com.example.silkroad.web.rest;

import com.example.silkroad.domain.Cart;
import com.example.silkroad.repository.CartRepository;
import com.example.silkroad.repository.CustomerRepository;
import com.example.silkroad.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.silkroad.domain.Cart}.
 */
@RestController
@RequestMapping("/api/carts")
@Transactional
public class CartResource {

    private static final Logger LOG = LoggerFactory.getLogger(CartResource.class);

    private static final String ENTITY_NAME = "cart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartRepository cartRepository;

    private final CustomerRepository customerRepository;

    public CartResource(CartRepository cartRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * {@code POST  /carts} : Create a new cart.
     *
     * @param cart the cart to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cart, or with status {@code 400 (Bad Request)} if the cart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Cart> createCart(@Valid @RequestBody Cart cart) throws URISyntaxException {
        LOG.debug("REST request to save Cart : {}", cart);
        if (cart.getId() != null) {
            throw new BadRequestAlertException("A new cart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(cart.getCustomer())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long customerId = cart.getCustomer().getId();
        customerRepository.findById(customerId).ifPresent(cart::customer);
        cart = cartRepository.save(cart);
        return ResponseEntity.created(new URI("/api/carts/" + cart.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cart.getId().toString()))
            .body(cart);
    }

    /**
     * {@code PUT  /carts/:id} : Updates an existing cart.
     *
     * @param id the id of the cart to save.
     * @param cart the cart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart,
     * or with status {@code 400 (Bad Request)} if the cart is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cart> updateCart(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Cart cart)
        throws URISyntaxException {
        LOG.debug("REST request to update Cart : {}, {}", id, cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cart = cartRepository.save(cart);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cart.getId().toString()))
            .body(cart);
    }

    /**
     * {@code PATCH  /carts/:id} : Partial updates given fields of an existing cart, field will ignore if it is null
     *
     * @param id the id of the cart to save.
     * @param cart the cart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart,
     * or with status {@code 400 (Bad Request)} if the cart is not valid,
     * or with status {@code 404 (Not Found)} if the cart is not found,
     * or with status {@code 500 (Internal Server Error)} if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Cart> partialUpdateCart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Cart cart
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Cart partially : {}, {}", id, cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Cart> result = cartRepository
            .findById(cart.getId())
            .map(existingCart -> {
                if (cart.getCreatedDate() != null) {
                    existingCart.setCreatedDate(cart.getCreatedDate());
                }

                return existingCart;
            })
            .map(cartRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cart.getId().toString())
        );
    }

    /**
     * {@code GET  /carts} : get all the carts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carts in body.
     */
    @GetMapping("")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Cart>> getAllCarts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Carts");
        Page<Cart> page;
        if (eagerload) {
            page = cartRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = cartRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /carts/:id} : get the "id" cart.
     *
     * @param id the id of the cart to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cart, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Cart> getCart(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Cart : {}", id);
        Optional<Cart> cart = cartRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(cart);
    }

    /**
     * {@code DELETE  /carts/:id} : delete the "id" cart.
     *
     * @param id the id of the cart to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Cart : {}", id);
        cartRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.example.silkroad.web.rest;

import com.example.silkroad.domain.OrderItem;
import com.example.silkroad.repository.OrderItemRepository;
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
 * REST controller for managing {@link com.example.silkroad.domain.OrderItem}.
 */
@RestController
@RequestMapping("/api/order-items")
@Transactional
public class OrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemResource.class);

    private static final String ENTITY_NAME = "orderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemRepository orderItemRepository;

    public OrderItemResource(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * {@code POST  /order-items} : Create a new orderItem.
     *
     * @param orderItem the orderItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItem, or with status {@code 400 (Bad Request)} if the orderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderItem> createOrderItem(@Valid @RequestBody OrderItem orderItem) throws URISyntaxException {
        LOG.debug("REST request to save OrderItem : {}", orderItem);
        if (orderItem.getId() != null) {
            throw new BadRequestAlertException("A new orderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderItem = orderItemRepository.save(orderItem);
        return ResponseEntity.created(new URI("/api/order-items/" + orderItem.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, orderItem.getId().toString()))
            .body(orderItem);
    }

    /**
     * {@code PUT  /order-items/:id} : Updates an existing orderItem.
     *
     * @param id the id of the orderItem to save.
     * @param orderItem the orderItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItem,
     * or with status {@code 400 (Bad Request)} if the orderItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderItem orderItem
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderItem : {}, {}", id, orderItem);
        if (orderItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderItem = orderItemRepository.save(orderItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItem.getId().toString()))
            .body(orderItem);
    }

    /**
     * {@code PATCH  /order-items/:id} : Partial updates given fields of an existing orderItem, field will ignore if it is null
     *
     * @param id the id of the orderItem to save.
     * @param orderItem the orderItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItem,
     * or with status {@code 400 (Bad Request)} if the orderItem is not valid,
     * or with status {@code 404 (Not Found)} if the orderItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderItem> partialUpdateOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderItem orderItem
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderItem partially : {}, {}", id, orderItem);
        if (orderItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderItem> result = orderItemRepository
            .findById(orderItem.getId())
            .map(existingOrderItem -> {
                if (orderItem.getQuantity() != null) {
                    existingOrderItem.setQuantity(orderItem.getQuantity());
                }
                if (orderItem.getUnitPrice() != null) {
                    existingOrderItem.setUnitPrice(orderItem.getUnitPrice());
                }

                return existingOrderItem;
            })
            .map(orderItemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItem.getId().toString())
        );
    }

    /**
     * {@code GET  /order-items} : get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrderItem>> getAllOrderItems(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of OrderItems");
        Page<OrderItem> page = orderItemRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-items/:id} : get the "id" orderItem.
     *
     * @param id the id of the orderItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrderItem : {}", id);
        Optional<OrderItem> orderItem = orderItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orderItem);
    }

    /**
     * {@code DELETE  /order-items/:id} : delete the "id" orderItem.
     *
     * @param id the id of the orderItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrderItem : {}", id);
        orderItemRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

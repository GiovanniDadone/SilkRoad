package com.example.silkroad.web.rest;

import static com.example.silkroad.domain.CartItemAsserts.*;
import static com.example.silkroad.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.silkroad.IntegrationTest;
import com.example.silkroad.domain.Cart;
import com.example.silkroad.domain.CartItem;
import com.example.silkroad.domain.Product;
import com.example.silkroad.repository.CartItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CartItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemMockMvc;

    private CartItem cartItem;

    private CartItem insertedCartItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createEntity(EntityManager em) {
        CartItem cartItem = new CartItem().quantity(DEFAULT_QUANTITY);
        // Add required entity
        Cart cart;
        if (TestUtil.findAll(em, Cart.class).isEmpty()) {
            cart = CartResourceIT.createEntity(em);
            em.persist(cart);
            em.flush();
        } else {
            cart = TestUtil.findAll(em, Cart.class).get(0);
        }
        cartItem.setCart(cart);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        cartItem.setProduct(product);
        return cartItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createUpdatedEntity(EntityManager em) {
        CartItem updatedCartItem = new CartItem().quantity(UPDATED_QUANTITY);
        // Add required entity
        Cart cart;
        if (TestUtil.findAll(em, Cart.class).isEmpty()) {
            cart = CartResourceIT.createUpdatedEntity(em);
            em.persist(cart);
            em.flush();
        } else {
            cart = TestUtil.findAll(em, Cart.class).get(0);
        }
        updatedCartItem.setCart(cart);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        updatedCartItem.setProduct(product);
        return updatedCartItem;
    }

    @BeforeEach
    void initTest() {
        cartItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCartItem != null) {
            cartItemRepository.delete(insertedCartItem);
            insertedCartItem = null;
        }
    }

    @Test
    @Transactional
    void createCartItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CartItem
        var returnedCartItem = om.readValue(
            restCartItemMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItem)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CartItem.class
        );

        // Validate the CartItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCartItemUpdatableFieldsEquals(returnedCartItem, getPersistedCartItem(returnedCartItem));

        insertedCartItem = returnedCartItem;
    }

    @Test
    @Transactional
    void createCartItemWithExistingId() throws Exception {
        // Create the CartItem with an existing ID
        cartItem.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItem)))
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cartItem.setQuantity(null);

        // Create the CartItem, which fails.

        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItem)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        // Get all the cartItemList
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        // Get the cartItem
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingCartItem() throws Exception {
        // Get the cartItem
        restCartItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCartItem are not directly saved in db
        em.detach(updatedCartItem);
        updatedCartItem.quantity(UPDATED_QUANTITY);

        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCartItem.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCartItemToMatchAllProperties(updatedCartItem);
    }

    @Test
    @Transactional
    void putNonExistingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItem.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cartItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cartItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCartItemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCartItem, cartItem), getPersistedCartItem(cartItem));
    }

    @Test
    @Transactional
    void fullUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        partialUpdatedCartItem.quantity(UPDATED_QUANTITY);

        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCartItemUpdatableFieldsEquals(partialUpdatedCartItem, getPersistedCartItem(partialUpdatedCartItem));
    }

    @Test
    @Transactional
    void patchNonExistingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cartItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cartItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cartItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cartItem
        restCartItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItem.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cartItemRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected CartItem getPersistedCartItem(CartItem cartItem) {
        return cartItemRepository.findById(cartItem.getId()).orElseThrow();
    }

    protected void assertPersistedCartItemToMatchAllProperties(CartItem expectedCartItem) {
        assertCartItemAllPropertiesEquals(expectedCartItem, getPersistedCartItem(expectedCartItem));
    }

    protected void assertPersistedCartItemToMatchUpdatableProperties(CartItem expectedCartItem) {
        assertCartItemAllUpdatablePropertiesEquals(expectedCartItem, getPersistedCartItem(expectedCartItem));
    }
}

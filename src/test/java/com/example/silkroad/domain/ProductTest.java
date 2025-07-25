package com.example.silkroad.domain;

import static com.example.silkroad.domain.CartItemTestSamples.*;
import static com.example.silkroad.domain.CategoryTestSamples.*;
import static com.example.silkroad.domain.OrderItemTestSamples.*;
import static com.example.silkroad.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.silkroad.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void categoryTest() {
        Product product = getProductRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        product.setCategory(categoryBack);
        assertThat(product.getCategory()).isEqualTo(categoryBack);

        product.category(null);
        assertThat(product.getCategory()).isNull();
    }

    @Test
    void cartItemTest() {
        Product product = getProductRandomSampleGenerator();
        CartItem cartItemBack = getCartItemRandomSampleGenerator();

        product.addCartItem(cartItemBack);
        assertThat(product.getCartItems()).containsOnly(cartItemBack);
        assertThat(cartItemBack.getProduct()).isEqualTo(product);

        product.removeCartItem(cartItemBack);
        assertThat(product.getCartItems()).doesNotContain(cartItemBack);
        assertThat(cartItemBack.getProduct()).isNull();

        product.cartItems(new HashSet<>(Set.of(cartItemBack)));
        assertThat(product.getCartItems()).containsOnly(cartItemBack);
        assertThat(cartItemBack.getProduct()).isEqualTo(product);

        product.setCartItems(new HashSet<>());
        assertThat(product.getCartItems()).doesNotContain(cartItemBack);
        assertThat(cartItemBack.getProduct()).isNull();
    }

    @Test
    void orderedItemsTest() {
        Product product = getProductRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        product.addOrderedItems(orderItemBack);
        assertThat(product.getOrderedItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getProduct()).isEqualTo(product);

        product.removeOrderedItems(orderItemBack);
        assertThat(product.getOrderedItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getProduct()).isNull();

        product.orderedItems(new HashSet<>(Set.of(orderItemBack)));
        assertThat(product.getOrderedItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getProduct()).isEqualTo(product);

        product.setOrderedItems(new HashSet<>());
        assertThat(product.getOrderedItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getProduct()).isNull();
    }
}

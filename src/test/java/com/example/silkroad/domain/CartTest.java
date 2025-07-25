package com.example.silkroad.domain;

import static com.example.silkroad.domain.CartItemTestSamples.*;
import static com.example.silkroad.domain.CartTestSamples.*;
import static com.example.silkroad.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.silkroad.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cart.class);
        Cart cart1 = getCartSample1();
        Cart cart2 = new Cart();
        assertThat(cart1).isNotEqualTo(cart2);

        cart2.setId(cart1.getId());
        assertThat(cart1).isEqualTo(cart2);

        cart2 = getCartSample2();
        assertThat(cart1).isNotEqualTo(cart2);
    }

    @Test
    void customerTest() {
        Cart cart = getCartRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        cart.setCustomer(customerBack);
        assertThat(cart.getCustomer()).isEqualTo(customerBack);

        cart.customer(null);
        assertThat(cart.getCustomer()).isNull();
    }

    @Test
    void cartItemsTest() {
        Cart cart = getCartRandomSampleGenerator();
        CartItem cartItemBack = getCartItemRandomSampleGenerator();

        cart.addCartItems(cartItemBack);
        assertThat(cart.getCartItems()).containsOnly(cartItemBack);
        assertThat(cartItemBack.getCart()).isEqualTo(cart);

        cart.removeCartItems(cartItemBack);
        assertThat(cart.getCartItems()).doesNotContain(cartItemBack);
        assertThat(cartItemBack.getCart()).isNull();

        cart.cartItems(new HashSet<>(Set.of(cartItemBack)));
        assertThat(cart.getCartItems()).containsOnly(cartItemBack);
        assertThat(cartItemBack.getCart()).isEqualTo(cart);

        cart.setCartItems(new HashSet<>());
        assertThat(cart.getCartItems()).doesNotContain(cartItemBack);
        assertThat(cartItemBack.getCart()).isNull();
    }
}

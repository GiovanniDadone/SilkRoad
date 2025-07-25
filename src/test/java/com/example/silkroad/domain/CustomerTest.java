package com.example.silkroad.domain;

import static com.example.silkroad.domain.CartTestSamples.*;
import static com.example.silkroad.domain.CustomerTestSamples.*;
import static com.example.silkroad.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.silkroad.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void ordersTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        customer.addOrders(orderBack);
        assertThat(customer.getOrders()).containsOnly(orderBack);
        assertThat(orderBack.getCustomer()).isEqualTo(customer);

        customer.removeOrders(orderBack);
        assertThat(customer.getOrders()).doesNotContain(orderBack);
        assertThat(orderBack.getCustomer()).isNull();

        customer.orders(new HashSet<>(Set.of(orderBack)));
        assertThat(customer.getOrders()).containsOnly(orderBack);
        assertThat(orderBack.getCustomer()).isEqualTo(customer);

        customer.setOrders(new HashSet<>());
        assertThat(customer.getOrders()).doesNotContain(orderBack);
        assertThat(orderBack.getCustomer()).isNull();
    }

    @Test
    void cartTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Cart cartBack = getCartRandomSampleGenerator();

        customer.setCart(cartBack);
        assertThat(customer.getCart()).isEqualTo(cartBack);
        assertThat(cartBack.getCustomer()).isEqualTo(customer);

        customer.cart(null);
        assertThat(customer.getCart()).isNull();
        assertThat(cartBack.getCustomer()).isNull();
    }
}

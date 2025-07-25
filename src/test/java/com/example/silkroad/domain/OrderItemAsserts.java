package com.example.silkroad.domain;

import static com.example.silkroad.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertOrderItemAllPropertiesEquals(OrderItem expected, OrderItem actual) {
        assertOrderItemAutoGeneratedPropertiesEquals(expected, actual);
        assertOrderItemAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertOrderItemAllUpdatablePropertiesEquals(OrderItem expected, OrderItem actual) {
        assertOrderItemUpdatableFieldsEquals(expected, actual);
        assertOrderItemUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertOrderItemAutoGeneratedPropertiesEquals(OrderItem expected, OrderItem actual) {
        assertThat(actual)
            .as("Verify OrderItem auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertOrderItemUpdatableFieldsEquals(OrderItem expected, OrderItem actual) {
        assertThat(actual)
            .as("Verify OrderItem relevant properties")
            .satisfies(a -> assertThat(a.getQuantity()).as("check quantity").isEqualTo(expected.getQuantity()))
            .satisfies(a ->
                assertThat(a.getUnitPrice()).as("check unitPrice").usingComparator(bigDecimalCompareTo).isEqualTo(expected.getUnitPrice())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertOrderItemUpdatableRelationshipsEquals(OrderItem expected, OrderItem actual) {
        assertThat(actual)
            .as("Verify OrderItem relationships")
            .satisfies(a -> assertThat(a.getOrder()).as("check order").isEqualTo(expected.getOrder()))
            .satisfies(a -> assertThat(a.getProduct()).as("check product").isEqualTo(expected.getProduct()));
    }
}

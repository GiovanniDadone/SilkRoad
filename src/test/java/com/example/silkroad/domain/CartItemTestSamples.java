package com.example.silkroad.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CartItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CartItem getCartItemSample1() {
        return new CartItem().id(1L).quantity(1);
    }

    public static CartItem getCartItemSample2() {
        return new CartItem().id(2L).quantity(2);
    }

    public static CartItem getCartItemRandomSampleGenerator() {
        return new CartItem().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}

package com.example.silkroad.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CartTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Cart getCartSample1() {
        return new Cart().id(1L);
    }

    public static Cart getCartSample2() {
        return new Cart().id(2L);
    }

    public static Cart getCartRandomSampleGenerator() {
        return new Cart().id(longCount.incrementAndGet());
    }
}

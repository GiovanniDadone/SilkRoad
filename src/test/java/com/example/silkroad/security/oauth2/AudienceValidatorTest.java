package com.example.silkroad.security.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Test class for the {@link AudienceValidator} utility class.
 */
class AudienceValidatorTest {

    private final AudienceValidator validator = new AudienceValidator(Arrays.asList("api://default"));

    @Test
    void testInvalidAudience() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "bar");
        Jwt badJwt = mock(Jwt.class);
        when(badJwt.getAudience()).thenReturn(List.of("bar"));
        assertThat(validator.validate(badJwt).hasErrors()).isTrue();
    }

    @Test
    void testValidAudience() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "api://default");
        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(List.of("api://default"));
        assertThat(validator.validate(jwt).hasErrors()).isFalse();
    }
}

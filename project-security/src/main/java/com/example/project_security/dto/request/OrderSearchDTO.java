package com.example.project_security.dto.request;

import java.time.ZonedDateTime;

import com.example.project_security.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la ricerca ordini
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSearchDTO {
    private Long userId;
    private OrderStatus status;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String trackingNumber;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "orderDate";

    @Builder.Default
    private String sortDirection = "DESC";
}
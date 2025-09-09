package com.Orders.OrdersMicroService.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private String orderNumber;
    private BigDecimal totalGross;
    private LocalDateTime createdAt;
}

package com.Orders.OrdersMicroService.model.dto.order;

import com.Orders.OrdersMicroService.common.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private BigDecimal totalGross;
    private String currency;
    private List<OrderItemDTO> items;
}

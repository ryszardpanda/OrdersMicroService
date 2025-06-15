package com.Orders.OrdersMicroService.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;
    private List<OrderItemConfigDTO> configurations;

}

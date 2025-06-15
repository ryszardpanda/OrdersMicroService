package com.Orders.OrdersMicroService.model.dto.order;

import com.Orders.OrdersMicroService.common.ProductsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemConfigDTO {
    private Long configId;
    private String name;
    private BigDecimal price;
    private ProductsType type;
}

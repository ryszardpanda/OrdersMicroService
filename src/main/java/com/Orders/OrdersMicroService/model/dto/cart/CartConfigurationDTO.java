package com.Orders.OrdersMicroService.model.dto.cart;

import com.Orders.OrdersMicroService.common.ProductsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartConfigurationDTO {
    private Long cartItemConfigurationId;
    private String name;
    private BigDecimal price;
    private ProductsType type;
}

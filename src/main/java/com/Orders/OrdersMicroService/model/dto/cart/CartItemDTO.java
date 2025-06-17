package com.Orders.OrdersMicroService.model.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private List<CartConfigurationDTO> configurations = new ArrayList<>();
    private String name;
    private BigDecimal price;
    private int quantity;
}

package com.Orders.OrdersMicroService.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {
    private String userId;
    private OrderAddressDTO shipping;
    private OrderAddressDTO billing;
    private List<OrderItemDTO> items;
}

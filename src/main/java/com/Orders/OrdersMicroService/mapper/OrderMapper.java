package com.Orders.OrdersMicroService.mapper;

import com.Orders.OrdersMicroService.model.dto.cart.CartItemDTO;
import com.Orders.OrdersMicroService.model.entity.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderItemEntity mapCartItemToOrderEntity(CartItemDTO cartItemDTO);
}

package com.Orders.OrdersMicroService.mapper;

import com.Orders.OrdersMicroService.model.dto.cart.CartConfigurationDTO;
import com.Orders.OrdersMicroService.model.dto.cart.CartItemDTO;
import com.Orders.OrdersMicroService.model.dto.order.CreateOrderRequestDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderResponseDTO;
import com.Orders.OrdersMicroService.model.entity.OrderEntity;
import com.Orders.OrdersMicroService.model.entity.OrderItemConfigEntity;
import com.Orders.OrdersMicroService.model.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "orderNumber", expression = "java(generateNumber())")
    OrderEntity toEntity(CreateOrderRequestDTO dto);

    OrderResponseDTO toDto(OrderEntity entity);

    // snapshot pozycji z koszyka
    @Mapping(target = "configurations", source = "configurations")
    OrderItemEntity toItemEntity(CartItemDTO cartItem);

    OrderItemConfigEntity toConfigEntity(CartConfigurationDTO cfg);

    private static String generateNumber() {
        return "ORD-" + LocalDate.now().getYear()
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

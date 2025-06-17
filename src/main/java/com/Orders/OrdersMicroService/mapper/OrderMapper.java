package com.Orders.OrdersMicroService.mapper;

import com.Orders.OrdersMicroService.model.dto.cart.CartConfigurationDTO;
import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import com.Orders.OrdersMicroService.model.dto.cart.CartItemDTO;
import com.Orders.OrdersMicroService.model.dto.order.CreateOrderRequestDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderAddressDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderResponseDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderSummaryDTO;
import com.Orders.OrdersMicroService.model.entity.OrderAddress;
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

    @Mapping(source = "price", target = "unitPrice")
    @Mapping(target = "configurations", source = "configurations")
    OrderItemEntity toItemEntity(CartItemDTO cartItem);

    @Mapping(source = "cartItemConfigurationId", target = "configId")
    OrderItemConfigEntity toConfigEntity(CartConfigurationDTO cfg);

    default String generateNumber() {
        return "ORD-" + LocalDate.now().getYear()
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    OrderEntity toOrderEntity(CartDTO cartDto);

    OrderSummaryDTO toSummary(OrderEntity orderEntity);

    OrderAddress toOrderAddress(OrderAddressDTO orderAddressDTO);
}

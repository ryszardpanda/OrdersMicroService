package com.Orders.OrdersMicroService.service;

import com.Orders.OrdersMicroService.client.CartClient;
import com.Orders.OrdersMicroService.mapper.OrderMapper;
import com.Orders.OrdersMicroService.model.dto.order.OrderSummaryDTO;
import com.Orders.OrdersMicroService.model.entity.OrderEntity;
import com.Orders.OrdersMicroService.repository.OrderEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class OrderServiceTest {

    private CartClient cartClient;
    private OrderEntityRepository orderRepository;
    private OrderMapper orderMapper;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.cartClient = Mockito.mock(CartClient.class);
        this.orderRepository = Mockito.mock(OrderEntityRepository.class);
        this.orderMapper = Mappers.getMapper(OrderMapper.class);
        this.orderService = new OrderService(cartClient, orderRepository, orderMapper);
    }

    @Test
    void getOrdersByUser_OrderSummaryPageReturned() {
// given
        String userId = "user123";
        Pageable pageable = PageRequest.of(0, 10);

        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setOrderNumber("ORD-123-123");
        entity.setTotalGross(BigDecimal.valueOf(222));
        entity.setCreatedAt(LocalDateTime.of(2022, 12, 12, 12, 12));

        Page<OrderEntity> entityPage = new PageImpl<>(List.of(entity));
        Mockito.when(orderRepository.findAllByUserId(userId, pageable)).thenReturn(entityPage);

        // when
        Page<OrderSummaryDTO> result = orderService.getOrdersByUser(userId, pageable);

        // then
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("ORD-123-123", result.getContent().get(0).getOrderNumber());
        Assertions.assertEquals(BigDecimal.valueOf(222), result.getContent().get(0).getTotalGross());
        Assertions.assertEquals(LocalDateTime.of(2022, 12, 12, 12, 12), result.getContent().get(0).getCreatedAt());

        Mockito.verify(orderRepository).findAllByUserId(userId, pageable);
    }
}

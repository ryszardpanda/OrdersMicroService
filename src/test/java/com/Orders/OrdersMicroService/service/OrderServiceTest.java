package com.Orders.OrdersMicroService.service;

import com.Orders.OrdersMicroService.client.CartClient;
import com.Orders.OrdersMicroService.common.OrderStatus;
import com.Orders.OrdersMicroService.exeption.EmptyCartException;
import com.Orders.OrdersMicroService.exeption.ForbiddenException;
import com.Orders.OrdersMicroService.mapper.OrderMapper;
import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import com.Orders.OrdersMicroService.model.dto.cart.CartItemDTO;
import com.Orders.OrdersMicroService.model.dto.order.*;
import com.Orders.OrdersMicroService.model.entity.OrderAddress;
import com.Orders.OrdersMicroService.model.entity.OrderEntity;
import com.Orders.OrdersMicroService.model.entity.OrderItemEntity;
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
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    void createOrder_OrderCreated(){
        //given
        CartItemDTO cartItemDTO = new CartItemDTO(1L, 1L, List.of(), "Item1", BigDecimal.valueOf(222), 5);
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, "Item1", BigDecimal.valueOf(222), 1, List.of());
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(1L, "ORD-123-123", LocalDateTime.of(2022, 12, 12, 12, 12), OrderStatus.NEW, BigDecimal.valueOf(222), "PLN", List.of(orderItemDTO));
        CartDTO cartDTO = new CartDTO(1L, "user123", List.of(cartItemDTO));
        OrderAddress shipping = new OrderAddress("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        OrderAddress billing = new OrderAddress("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");


        OrderEntity order = new OrderEntity(
                1L,
                "ORD-123-123",
                LocalDateTime.of(2022, 12, 12, 12, 12),     // createdAt
                OrderStatus.NEW,
                "user123",
                shipping,
                billing,
                BigDecimal.valueOf(222),
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(0),
                "PLN",
               new ArrayList<>()
        );

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setId(1L);
        orderItemEntity.setProductId(1L);
        orderItemEntity.setName("Item1");
        orderItemEntity.setUnitPrice(BigDecimal.valueOf(222));
        orderItemEntity.setQuantity(1);
        orderItemEntity.setOrder(order);

        order.getItems().add(orderItemEntity);

        OrderAddressDTO shippingAddresDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        OrderAddressDTO billingAddresDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");

        CreateOrderRequestDTO createOrderRequestDTO = new CreateOrderRequestDTO(shippingAddresDTO, billingAddresDTO);

        Mockito.when(cartClient.getCartById(cartDTO.getCartId())).thenReturn(cartDTO);
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);

        //when
        OrderResponseDTO result = orderService.createOrder(cartDTO.getUserId(), cartDTO.getCartId(), createOrderRequestDTO);

        //then
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("ORD-123-123", result.getOrderNumber());
        Assertions.assertEquals(LocalDateTime.of(2022, 12, 12, 12, 12), result.getCreatedAt());
        Assertions.assertEquals(OrderStatus.NEW, result.getStatus());
        Assertions.assertEquals(BigDecimal.valueOf(300), result.getTotalGross());
        Assertions.assertEquals("PLN", result.getCurrency());
        Assertions.assertEquals(1, result.getItems().size());
        OrderItemDTO firstItem = result.getItems().get(0);
        Assertions.assertEquals(1L, firstItem.getProductId());
        Assertions.assertEquals("Item1", firstItem.getName());
        Assertions.assertEquals(BigDecimal.valueOf(222), firstItem.getUnitPrice());
    }

    @Test
    void createOrder_EmptyCart_ThrowsEmptyCartException() {
        //given
        String userId = "user123";
        Long cartId = 1L;
        CartDTO emptyCartDTO = new CartDTO(cartId, userId, List.of()); // Pusta lista items

        OrderAddressDTO shippingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        OrderAddressDTO billingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        CreateOrderRequestDTO createOrderRequestDTO = new CreateOrderRequestDTO(shippingAddressDTO, billingAddressDTO);

        Mockito.when(cartClient.getCartById(cartId)).thenReturn(emptyCartDTO);

        //when & then
        EmptyCartException exception = Assertions.assertThrows(EmptyCartException.class, () -> {
            orderService.createOrder(userId, cartId, createOrderRequestDTO);
        });

        Assertions.assertEquals("Cart is empty", exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        Mockito.verify(cartClient, Mockito.times(1)).getCartById(cartId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createOrder_NullCart_ThrowsEmptyCartException() {
        //given
        String userId = "user123";
        Long cartId = 1L;

        OrderAddressDTO shippingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        OrderAddressDTO billingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        CreateOrderRequestDTO createOrderRequestDTO = new CreateOrderRequestDTO(shippingAddressDTO, billingAddressDTO);

        Mockito.when(cartClient.getCartById(cartId)).thenReturn(null); // Zwraca null

        //when & then
        EmptyCartException exception = Assertions.assertThrows(EmptyCartException.class, () -> {
            orderService.createOrder(userId, cartId, createOrderRequestDTO);
        });

        Assertions.assertEquals("Cart is empty", exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        Mockito.verify(cartClient, Mockito.times(1)).getCartById(cartId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createOrder_CartBelongsToAnotherUser_ThrowsForbiddenException() {
        //given
        String userId = "user123";
        String differentUserId = "user456"; // Inny użytkownik
        Long cartId = 1L;

        CartItemDTO cartItemDTO = new CartItemDTO(1L, 1L, List.of(), "Item1", BigDecimal.valueOf(222), 5);
        CartDTO cartDTO = new CartDTO(cartId, differentUserId, List.of(cartItemDTO)); // Cart należy do innego użytkownika

        OrderAddressDTO shippingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        OrderAddressDTO billingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        CreateOrderRequestDTO createOrderRequestDTO = new CreateOrderRequestDTO(shippingAddressDTO, billingAddressDTO);

        Mockito.when(cartClient.getCartById(cartId)).thenReturn(cartDTO);

        //when & then
        ForbiddenException exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            orderService.createOrder(userId, cartId, createOrderRequestDTO);
        });

        Assertions.assertEquals("Cart is not connected to user: " + userId, exception.getMessage());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());

        Mockito.verify(cartClient, Mockito.times(1)).getCartById(cartId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createOrder_CartUserIdIsNull_ThrowsForbiddenException() {
        //given
        String userId = "user123";
        Long cartId = 1L;

        CartItemDTO cartItemDTO = new CartItemDTO(1L, 1L, List.of(), "Item1", BigDecimal.valueOf(222), 5);
        CartDTO cartDTO = new CartDTO(cartId, null, List.of(cartItemDTO)); // Cart ma null userId

        OrderAddressDTO shippingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        OrderAddressDTO billingAddressDTO = new OrderAddressDTO("name1", "lastName1", "Street1", "Lodz", "93-335", "1212212121");
        CreateOrderRequestDTO createOrderRequestDTO = new CreateOrderRequestDTO(shippingAddressDTO, billingAddressDTO);

        Mockito.when(cartClient.getCartById(cartId)).thenReturn(cartDTO);

        //when & then
        ForbiddenException exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            orderService.createOrder(userId, cartId, createOrderRequestDTO);
        });

        Assertions.assertEquals("Cart is not connected to user: " + userId, exception.getMessage());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());

        Mockito.verify(cartClient, Mockito.times(1)).getCartById(cartId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }
}

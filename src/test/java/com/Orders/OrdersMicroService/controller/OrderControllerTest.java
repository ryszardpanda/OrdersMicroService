package com.Orders.OrdersMicroService.controller;

import com.Orders.OrdersMicroService.exeption.EmptyCartException;
import com.Orders.OrdersMicroService.exeption.ForbiddenException;
import com.Orders.OrdersMicroService.model.dto.order.CreateOrderRequestDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderAddressDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderResponseDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderSummaryDTO;
import com.Orders.OrdersMicroService.service.OrderService;
import com.Orders.OrdersMicroService.common.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrdersByUser_ValidRequest_ReturnsOrdersPage() throws Exception {
        // given
        String userId = "user123";
        Pageable pageable = PageRequest.of(0, 10);

        OrderSummaryDTO orderSummary = new OrderSummaryDTO(1L, "ORD-2025-12345",
                BigDecimal.valueOf(299.99), LocalDateTime.of(2025, 1, 15, 10, 30)
        );

        Page<OrderSummaryDTO> ordersPage = new PageImpl<>(List.of(orderSummary), pageable, 1);

        Mockito.when(orderService.getOrdersByUser(eq(userId), any(Pageable.class)))
                .thenReturn(ordersPage);

        // when & then
        mockMvc.perform(get("/api/order")
                        .param("userId", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-2025-12345"))
                .andExpect(jsonPath("$.content[0].totalGross").value(299.99))
                .andExpect(jsonPath("$.content[0].createdAt").value("2025-01-15T10:30:00"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        Mockito.verify(orderService, Mockito.times(1)).getOrdersByUser(eq(userId), any(Pageable.class));
    }

    @Test
    void getOrdersByUser_EmptyResult_ReturnsEmptyPage() throws Exception {
        // given
        String userId = "user123";
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderSummaryDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(orderService.getOrdersByUser(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        // when & then
        mockMvc.perform(get("/api/order")
                        .param("userId", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        Mockito.verify(orderService, Mockito.times(1)).getOrdersByUser(eq(userId), any(Pageable.class));
    }

    @Test
    void getOrdersByUser_MissingUserIdParameter_ReturnsBadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/order")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        Mockito.verify(orderService, Mockito.never()).getOrdersByUser(any(), any());
    }

    @Test
    void placeOrder_ValidRequest_ReturnsOrderResponse() throws Exception {
        // given
        Long cartId = 1L;
        String userId = "user123";

        OrderAddressDTO shippingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        OrderAddressDTO billingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        CreateOrderRequestDTO requestDTO = new CreateOrderRequestDTO(shippingAddress, billingAddress);

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                1L,
                "ORD-2025-12345",
                LocalDateTime.of(2025, 1, 15, 10, 30),
                OrderStatus.NEW,
                BigDecimal.valueOf(299.99),
                "PLN",
                List.of()
        );

        Mockito.when(orderService.createOrder(userId, cartId, requestDTO))
                .thenReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/api/order/cart/{cartId}/user/{userId}", cartId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-2025-12345"))
                .andExpect(jsonPath("$.status").value("New"))
                .andExpect(jsonPath("$.totalGross").value(299.99))
                .andExpect(jsonPath("$.currency").value("PLN"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0));

        Mockito.verify(orderService, Mockito.times(1)).createOrder(userId, cartId, requestDTO);
    }

    @Test
    void placeOrder_EmptyCart_ReturnsBadRequest() throws Exception {
        // given
        Long cartId = 1L;
        String userId = "user123";

        OrderAddressDTO shippingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        OrderAddressDTO billingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        CreateOrderRequestDTO requestDTO = new CreateOrderRequestDTO(shippingAddress, billingAddress);

        Mockito.when(orderService.createOrder(userId, cartId, requestDTO))
                .thenThrow(new EmptyCartException("Cart is empty", HttpStatus.BAD_REQUEST));

        // when & then
        mockMvc.perform(post("/api/order/cart/{cartId}/user/{userId}", cartId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(orderService, Mockito.times(1)).createOrder(userId, cartId, requestDTO);
    }

    @Test
    void placeOrder_CartBelongsToAnotherUser_ReturnsForbidden() throws Exception {
        // given
        Long cartId = 1L;
        String userId = "user123";

        OrderAddressDTO shippingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        OrderAddressDTO billingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        CreateOrderRequestDTO requestDTO = new CreateOrderRequestDTO(shippingAddress, billingAddress);

        Mockito.when(orderService.createOrder(userId, cartId, requestDTO))
                .thenThrow(new ForbiddenException("Cart is not connected to user: " + userId, HttpStatus.FORBIDDEN));

        // when & then
        mockMvc.perform(post("/api/order/cart/{cartId}/user/{userId}", cartId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());

        Mockito.verify(orderService, Mockito.times(1)).createOrder(userId, cartId, requestDTO);
    }

    @Test
    void placeOrder_InvalidJson_ReturnsBadRequest() throws Exception {
        // given
        Long cartId = 1L;
        String userId = "user123";
        String invalidJson = "{ invalid json }";

        // when & then
        mockMvc.perform(post("/api/order/cart/{cartId}/user/{userId}", cartId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        Mockito.verify(orderService, Mockito.never()).createOrder(any(), any(), any());
    }

    @Test
    void placeOrder_MissingContentType_ReturnsUnsupportedMediaType() throws Exception {
        // given
        Long cartId = 1L;
        String userId = "user123";

        OrderAddressDTO shippingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        OrderAddressDTO billingAddress = new OrderAddressDTO(
                "John", "Doe", "Main St 123", "Warsaw", "00-001", "123456789"
        );
        CreateOrderRequestDTO requestDTO = new CreateOrderRequestDTO(shippingAddress, billingAddress);

        // when & then
        mockMvc.perform(post("/api/order/cart/{cartId}/user/{userId}", cartId, userId)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnsupportedMediaType());

        Mockito.verify(orderService, Mockito.never()).createOrder(any(), any(), any());
    }
}
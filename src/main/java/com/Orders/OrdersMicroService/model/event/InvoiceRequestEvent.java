package com.Orders.OrdersMicroService.model.event;

import com.Orders.OrdersMicroService.model.dto.order.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequestEvent {
    private Long orderId;
    private String orderNumber;
    private String userId;

    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String zip;
    private String phone;

    private BigDecimal totalNet;
    private BigDecimal totalGross;
    private BigDecimal shippingCost;
    private String currency;

    private List<OrderItemDTO> items;

    private LocalDateTime orderCreatedAt;
}
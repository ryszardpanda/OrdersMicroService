package com.Orders.OrdersMicroService.model.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDto {
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineNet;
    private BigDecimal lineGross;
}

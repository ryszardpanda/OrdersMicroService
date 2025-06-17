package com.Orders.OrdersMicroService.model.entity;

import com.Orders.OrdersMicroService.common.ProductsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "order_item_config")
@Getter
@Setter
@NoArgsConstructor

public class OrderItemConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItemEntity orderItem;

    private Long configId;                  // ID konfiguracji w mikroserwisie produktów
    private String name;
    @Column(scale = 2)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private ProductsType type;
}

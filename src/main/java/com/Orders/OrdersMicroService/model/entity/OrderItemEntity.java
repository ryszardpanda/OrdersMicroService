package com.Orders.OrdersMicroService.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id")
    private OrderEntity order;

    private Long productId;
    private String name;
    @Column(scale = 2) private BigDecimal unitPrice;
    private int quantity;
    @Column(scale = 2) private BigDecimal lineNet;
    @Column(scale = 2) private BigDecimal lineGross;

    @OneToMany(mappedBy = "orderItem", cascade = ALL, orphanRemoval = true)
    private List<OrderItemConfigEntity> configurations = new ArrayList<>();
}

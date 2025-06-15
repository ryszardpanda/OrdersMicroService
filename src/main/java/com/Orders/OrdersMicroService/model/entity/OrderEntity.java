package com.Orders.OrdersMicroService.model.entity;

import com.Orders.OrdersMicroService.common.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                         // PK w bazie

    @Column(nullable = false, unique = true, length = 30)
    private String orderNumber;              // np. ORD-2025-000123

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.NEW;

    @Column(nullable = false, length = 36)
    private String userId;                   // identyfikator klienta z Auth MS

    // ---------- adresy ----------
    @Embedded @AttributeOverrides({
            @AttributeOverride(name = "firstName",  column=@Column(name="ship_first_name")),
            @AttributeOverride(name = "lastName",   column=@Column(name="ship_last_name")),
            @AttributeOverride(name = "street",     column=@Column(name="ship_street")),
            @AttributeOverride(name = "city",       column=@Column(name="ship_city")),
            @AttributeOverride(name = "zip",        column=@Column(name="ship_zip")),
            @AttributeOverride(name = "phone",      column=@Column(name="ship_phone"))
    })
    private OrderAddress shipping;

    @Embedded @AttributeOverrides({
            @AttributeOverride(name = "firstName",  column=@Column(name="bill_first_name")),
            @AttributeOverride(name = "lastName",   column=@Column(name="bill_last_name")),
            @AttributeOverride(name = "street",     column=@Column(name="bill_street")),
            @AttributeOverride(name = "city",       column=@Column(name="bill_city")),
            @AttributeOverride(name = "zip",        column=@Column(name="bill_zip")),
            @AttributeOverride(name = "phone",      column=@Column(name="bill_phone"))
    })
    private OrderAddress billing;

    // ---------- wynik finansowy ----------
    @Column(nullable = false, scale = 2)
    private BigDecimal totalNet;             // suma pozycji netto
    @Column(nullable = false, scale = 2)
    private BigDecimal totalGross;           // + podatek
    @Column(nullable = false, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;
    @Column(nullable = false, length = 3)
    private String currency = "PLN";

    // ---------- relacja ----------
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();
}

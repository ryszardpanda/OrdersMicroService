package com.Orders.OrdersMicroService.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddress {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String zip;
    private String phone;
}

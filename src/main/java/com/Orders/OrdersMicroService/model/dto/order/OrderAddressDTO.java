package com.Orders.OrdersMicroService.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddressDTO {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String zip;
    private String phone;
}

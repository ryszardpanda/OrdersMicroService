package com.Orders.OrdersMicroService.model.dto.order;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderAddressDTO {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String zip;
    private String phone;
}

package com.Orders.OrdersMicroService.common;

import com.Orders.OrdersMicroService.exeption.WrongTypeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum ProductsType {
    ELECTRONICS("Electronics"),
    SMARTPHONE("Smartphone"),
    COMPUTER("Computer"),
    RAM("Ram"),
    PROCESSOR("Processor"),
    ACCESSORY("Accessory"),
    PRODUCT("Product");

    private String name;


    ProductsType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ProductsType from(String name){
        return Arrays.stream(ProductsType.values())
                .filter(productsType -> productsType.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new WrongTypeException("Unknow enum value: " + name, HttpStatus.BAD_REQUEST));
    }
}

package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;

    public OrderNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
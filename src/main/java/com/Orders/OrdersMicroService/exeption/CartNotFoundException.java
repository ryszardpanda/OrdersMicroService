package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CartNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;

    public CartNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

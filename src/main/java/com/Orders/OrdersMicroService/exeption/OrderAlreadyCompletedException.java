package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderAlreadyCompletedException extends RuntimeException {
    private HttpStatus httpStatus;

    public OrderAlreadyCompletedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
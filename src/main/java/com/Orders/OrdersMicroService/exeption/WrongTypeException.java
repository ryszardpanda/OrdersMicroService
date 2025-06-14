package com.Orders.OrdersMicroService.exeption;

import org.springframework.http.HttpStatus;

public class WrongTypeException extends RuntimeException{
    private final HttpStatus httpStatus;

    public WrongTypeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

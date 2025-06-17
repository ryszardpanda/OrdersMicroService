package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WrongTypeException extends RuntimeException{
    private final HttpStatus httpStatus;

    public WrongTypeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyCartException extends RuntimeException{
    private HttpStatus httpStatus;

    public EmptyCartException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

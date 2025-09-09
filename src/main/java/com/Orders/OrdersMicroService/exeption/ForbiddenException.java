package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
private HttpStatus httpStatus;

    public ForbiddenException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

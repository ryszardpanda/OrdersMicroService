package com.Orders.OrdersMicroService.exeption.handler;

import com.Orders.OrdersMicroService.exeption.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler({EmptyCartException.class})
    public ResponseEntity<ErrorMessage> handleEmptyCartException(
            EmptyCartException ex) {
        return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(ex.getMessage()), new HttpHeaders(), ex.getHttpStatus());
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ErrorMessage> handleForbiddenException(
            ForbiddenException ex) {
        return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(ex.getMessage()), new HttpHeaders(), ex.getHttpStatus());
    }

    @ExceptionHandler({WrongTypeException.class})
    public ResponseEntity<ErrorMessage> handleWrongTypeException(
            WrongTypeException ex) {
        return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(ex.getMessage()), new HttpHeaders(), ex.getHttpStatus());
    }

    @ExceptionHandler({CartNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleCartNotFoundException(
            CartNotFoundException ex) {
        return new ResponseEntity<ErrorMessage>(
                new ErrorMessage(ex.getMessage()), new HttpHeaders(), ex.getHttpStatus());
    }
}

package com.Orders.OrdersMicroService.exeption;

import lombok.Getter;

@Getter
public class EventPublishingException extends RuntimeException {
    private final String orderNumber;
    private final String eventType;

    public EventPublishingException(String message, String orderNumber, String eventType) {
        super(message);
        this.orderNumber = orderNumber;
        this.eventType = eventType;
    }

    public EventPublishingException(String message, String orderNumber, String eventType, Throwable cause) {
        super(message, cause);
        this.orderNumber = orderNumber;
        this.eventType = eventType;
    }
}
package com.Orders.OrdersMicroService.rabbit;

import com.Orders.OrdersMicroService.exeption.EventPublishingException;
import com.Orders.OrdersMicroService.model.event.InvoiceRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange.invoice:invoice.exchange}")
    private String invoiceExchange;

    @Value("${rabbit.routing-key.invoice.create:invoice.create}")
    private String invoiceRoutingKey;


    public void publishInvoiceRequest(InvoiceRequestEvent event) {
        try {
            rabbitTemplate.convertAndSend(invoiceExchange, invoiceRoutingKey, event);
            log.info("Successfully published invoice request for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to publish invoice request for order: {}", event.getOrderNumber(), e);
            throw new EventPublishingException(
                    "Failed to publish invoice event",
                    event.getOrderNumber(),
                    "INVOICE_REQUEST",
                    e
            );
        }
    }
}

package com.Orders.OrdersMicroService.service;

import com.Orders.OrdersMicroService.client.CartClient;
import com.Orders.OrdersMicroService.common.OrderStatus;
import com.Orders.OrdersMicroService.exeption.*;
import com.Orders.OrdersMicroService.mapper.OrderMapper;
import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import com.Orders.OrdersMicroService.model.dto.order.CreateOrderRequestDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderResponseDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderSummaryDTO;
import com.Orders.OrdersMicroService.model.entity.OrderEntity;
import com.Orders.OrdersMicroService.model.entity.OrderItemConfigEntity;
import com.Orders.OrdersMicroService.model.entity.OrderItemEntity;
import com.Orders.OrdersMicroService.model.event.InvoiceRequestEvent;
import com.Orders.OrdersMicroService.rabbit.OrderEventPublisher;
import com.Orders.OrdersMicroService.repository.OrderEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CartClient cartClient;
    private final OrderEntityRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher eventPublisher;
    private static final BigDecimal VAT_MULTIPLIER = BigDecimal.valueOf(1.23);

    public Page<OrderSummaryDTO> getOrdersByUser(String userId, Pageable p) {
        return orderRepository.findAllByUserId(userId, p)
                .map(orderMapper::toSummary);
    }

    @Transactional
    public void finalizeOrder(String orderId) {
        OrderEntity order = orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new OrderAlreadyCompletedException("Order " + order.getOrderNumber() + " is already completed", HttpStatus.CONFLICT);
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        publishInvoiceEvent(order);

        log.info("Order {} finalized and invoice request sent", order.getOrderNumber());
    }

    @Transactional
    public OrderResponseDTO createOrder(String userId, Long cartId, CreateOrderRequestDTO createOrderRequestDTO) {

        CartDTO cartDTO = checkCart(cartId, userId);

        OrderEntity order = checkCorrectnessOfOrder(userId, createOrderRequestDTO, cartDTO);

        for (OrderItemEntity item : order.getItems()) {
            if (item.getConfigurations() != null) {
                for (OrderItemConfigEntity config : item.getConfigurations()) {
                    config.setOrderItem(item);
                }
            }
        }

        OrderEntity orderWithPriceInfo = calculateAndSetPriceOfOrder(order);

        OrderEntity savedOrder = orderRepository.save(orderWithPriceInfo);
        log.info("Created order {} for user {}", savedOrder.getOrderNumber(), userId);

        return orderMapper.toDto(savedOrder);
    }


    private CartDTO checkCart(Long cartId, String userId) {
        CartDTO cart = cartClient.getCartById(cartId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        if (!userId.equals(cart.getUserId())) {
            throw new ForbiddenException("Cart is not connected to user: " + userId, HttpStatus.FORBIDDEN);
        }
        return cart;
    }

    private OrderEntity checkCorrectnessOfOrder(String userId, CreateOrderRequestDTO createOrderRequestDTO, CartDTO cart) {

        OrderEntity order = orderMapper.toOrderEntity(cart);
        order.setUserId(userId);
        order.setOrderNumber(generateNumber());

        if (createOrderRequestDTO != null) {
            if (createOrderRequestDTO.getShipping() != null) {
                order.setShipping(orderMapper.toOrderAddress(createOrderRequestDTO.getShipping()));
            }
            if (createOrderRequestDTO.getBilling() != null) {
                order.setBilling(orderMapper.toOrderAddress(createOrderRequestDTO.getBilling()));
            }

            log.info("ORDER.shipping = {}", order.getShipping());
        }

        return order;
    }

    private OrderEntity calculateAndSetPriceOfOrder(OrderEntity order) {
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalGross = BigDecimal.ZERO;

        for (OrderItemEntity item : order.getItems()) {
            BigDecimal lineNet = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal lineGross = lineNet.multiply(VAT_MULTIPLIER)
                    .setScale(2, RoundingMode.HALF_UP);

            item.setLineNet(lineNet);
            item.setLineGross(lineGross);
            item.setOrder(order);

            totalNet = totalNet.add(lineNet);
            totalGross = totalGross.add(lineGross);
        }

        order.setTotalNet(totalNet);
        order.setTotalGross(totalGross);

        return order;
    }

    private String generateNumber() {
        int rand = ThreadLocalRandom.current().nextInt(10000, 99999);
        return "ORD-" + LocalDate.now().getYear() + "-" + rand;
    }

    private void publishInvoiceEvent(OrderEntity order) {
        try {
            InvoiceRequestEvent invoiceEvent = orderMapper.toInvoiceRequestEvent(order);
            eventPublisher.publishInvoiceRequest(invoiceEvent);
            log.debug("Invoice event published for order: {}", order.getOrderNumber());
        } catch (EventPublishingException e) {
            log.error("Failed to publish invoice event for order: {}", order.getOrderNumber(), e);
            throw e;
        }
    }
}

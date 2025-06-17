package com.Orders.OrdersMicroService.service;

import com.Orders.OrdersMicroService.client.CartClient;
import com.Orders.OrdersMicroService.exeption.EmptyCartException;
import com.Orders.OrdersMicroService.exeption.ForbiddenException;
import com.Orders.OrdersMicroService.mapper.OrderMapper;
import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import com.Orders.OrdersMicroService.model.dto.order.CreateOrderRequestDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderResponseDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderSummaryDTO;
import com.Orders.OrdersMicroService.model.entity.OrderEntity;
import com.Orders.OrdersMicroService.model.entity.OrderItemEntity;
import com.Orders.OrdersMicroService.repository.OrderEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;


    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class OrderService {

        private final CartClient cartClient;
        private final OrderEntityRepository orderRepository;
        private final OrderMapper orderMapper;

        public CartDTO getCartById(Long cartId){
            return cartClient.getCartById(cartId);
        }

        public Page<OrderSummaryDTO> getOrdersByUser(String userId, Pageable p) {
            return orderRepository.findAllByUserId(userId, p)
                    .map(orderMapper::toSummary);
        }

        /**
         * Tworzy zamówienie na podstawie koszyka danego użytkownika
         *
         * @param userId identyfikator użytkownika (z JWT albo parametru)
         * @param cartId identyfikator koszyka (jeśli koszyków może być wiele)
         * @return DTO nowo utworzonego zamówienia
         */
        @Transactional
        public OrderResponseDTO createOrder(String userId, Long cartId, CreateOrderRequestDTO createOrderRequestDTO) {

            CartDTO cart = cartClient.getCartById(cartId);
            if (cart == null || cart.getItems().isEmpty()) {
                throw new EmptyCartException("Cart is empty", HttpStatus.BAD_REQUEST);
            }

            if (!userId.equals(cart.getUserId())) {
                throw new ForbiddenException("Cart is not connected to user: " + userId, HttpStatus.NOT_FOUND);
            }

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

            BigDecimal totalNet   = BigDecimal.ZERO;
            BigDecimal totalGross = BigDecimal.ZERO;

            for (OrderItemEntity item : order.getItems()) {
                BigDecimal lineNet   = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal lineGross = lineNet;
                item.setLineNet(lineNet);
                item.setLineGross(lineGross);

                totalNet   = totalNet.add(lineNet);
                totalGross = totalGross.add(lineGross);
                item.setOrder(order);
            }

            order.setTotalNet(totalNet);
            order.setTotalGross(totalGross);

            OrderEntity saved = orderRepository.save(order);
            log.info("Created order {} for user {}", saved.getOrderNumber(), userId);

            return orderMapper.toDto(saved);
        }

        /** Generowanie publicznego numeru zamówienia, np. ORD-2025-00123 */
        private String generateNumber() {
            int rand = ThreadLocalRandom.current().nextInt(10000, 99999);
            return "ORD-" + LocalDate.now().getYear() + "-" + rand;
        }
}

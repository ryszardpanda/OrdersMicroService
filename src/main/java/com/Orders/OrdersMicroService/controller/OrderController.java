package com.Orders.OrdersMicroService.controller;

import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import com.Orders.OrdersMicroService.model.dto.order.CreateOrderRequestDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderResponseDTO;
import com.Orders.OrdersMicroService.model.dto.order.OrderSummaryDTO;
import com.Orders.OrdersMicroService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/order")
public class OrderController {

  private final OrderService orderService;
    //Controlna metoda sprawdzajaca feigna
    @GetMapping(value="/{cartId}", produces= MediaType.APPLICATION_JSON_VALUE)
      CartDTO getCart(@PathVariable Long cartId){
        CartDTO cartById = orderService.getCartById(cartId);
        log.info(cartById.toString());
        return cartById;
    }

  @GetMapping
  public Page<OrderSummaryDTO> getOrdersByUser(@RequestParam String userId, @ParameterObject Pageable pageable) {
      log.info("New request for endpoint GET/api/order registered");
    return orderService.getOrdersByUser(userId, pageable);
  }

  @PostMapping(value = "/cart/{cartId}/user/{userId}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public OrderResponseDTO placeOrder(@PathVariable Long cartId, @PathVariable String userId, @RequestBody CreateOrderRequestDTO body) {
    log.info("New request for endpoint POST/api/order/cart/{cartId}/user/{userId} registered");
    return orderService.createOrder(userId, cartId, body);
  }
}

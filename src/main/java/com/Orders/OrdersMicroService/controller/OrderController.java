package com.Orders.OrdersMicroService.controller;

import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import com.Orders.OrdersMicroService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

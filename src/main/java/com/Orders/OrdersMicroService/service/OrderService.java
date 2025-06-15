package com.Orders.OrdersMicroService.service;

import com.Orders.OrdersMicroService.client.CartClient;
import com.Orders.OrdersMicroService.model.dto.cart.CartDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final CartClient cartClient;

  public CartDTO getCartById(Long cartId){
        return cartClient.getCartById(cartId);
    }
}

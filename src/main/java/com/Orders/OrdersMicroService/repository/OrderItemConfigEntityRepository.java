package com.Orders.OrdersMicroService.repository;

import com.Orders.OrdersMicroService.model.entity.OrderItemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemConfigEntityRepository extends JpaRepository<OrderItemConfigEntity, Long> {
}

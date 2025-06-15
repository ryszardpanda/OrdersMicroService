package com.Orders.OrdersMicroService.repository;

import com.Orders.OrdersMicroService.model.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, Long> {
}

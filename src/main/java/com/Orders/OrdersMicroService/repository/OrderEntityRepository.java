package com.Orders.OrdersMicroService.repository;

import com.Orders.OrdersMicroService.model.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAllByUserId(String userId, Pageable pageable);
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
}

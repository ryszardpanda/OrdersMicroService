package com.Orders.OrdersMicroService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrdersMicroServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersMicroServiceApplication.class, args);
	}

}

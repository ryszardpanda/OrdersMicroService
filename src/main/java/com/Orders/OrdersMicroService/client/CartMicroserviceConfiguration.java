package com.Orders.OrdersMicroService.client;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartMicroserviceConfiguration {

//    @Bean
//    public ErrorDecoder productFetcherErrorDecoder(){
//        return new ProductErrorDecoder();
//    }

    @Bean
    public Retryer feignRetryer(){
        return new Retryer.Default(100, 500, 10);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}

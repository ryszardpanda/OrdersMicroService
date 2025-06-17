package com.Orders.OrdersMicroService.exeption.errorDecoder;

import com.Orders.OrdersMicroService.exeption.CartNotFoundException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class CartErrorDecoder implements ErrorDecoder{
    private ErrorDecoder errorDecoder = new ErrorDecoder.Default();
    private static final Logger logger = LoggerFactory.getLogger(CartErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = feign.FeignException.errorStatus(methodKey, response);
        RetryableException retryableException = new RetryableException(response.status(),
                exception.getMessage(),
                response.request().httpMethod(),
                exception,
                500L,
                response.request());
        switch (response.status()) {
            case 400, 404:
                return new CartNotFoundException("Cart with provided id NOT FOUND, please ensure correctness of data and try again", HttpStatus.NOT_FOUND);
            case 500:
            case 502:
            case 503:
            case 504:
                logger.info("Throwing RetryableException and Retrying the request", response.status());
                return retryableException;
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}

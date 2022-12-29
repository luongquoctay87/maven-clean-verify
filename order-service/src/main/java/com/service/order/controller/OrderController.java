package com.service.order.controller;

import com.service.order.dto.OrderRequest;
import com.service.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/orders")
@Slf4j(topic = "ORDER-CONTROLLER")
@Tag(name = "Order Controller")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place Order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest request) {
        log.info("Request POST /order-service/orders");
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(request));
    }

    private CompletableFuture<String> fallbackMethod(OrderRequest request, RuntimeException exception) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after few minutes");
    }
}

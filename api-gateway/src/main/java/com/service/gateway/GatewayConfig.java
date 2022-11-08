package com.service.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Autowired
    private GlobalPreFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/product-service/**").filters(f -> f.filter(filter)).uri("lb://product-service"))
                .route("order-service", r -> r.path("/api/order-service/**").filters(f -> f.filter(filter)).uri("lb://order-service"))
                .route("inventory-service", r -> r.path("/api/inventory-service/**").filters(f -> f.filter(filter)).uri("lb://inventory-service"))
                .build();
    }
}

package com.service.order.service;

import com.service.order.dto.InventoryResponse;
import com.service.order.dto.OrderLineItemDto;
import com.service.order.dto.OrderRequest;
import com.service.order.event.PlacedOrderEvent;
import com.service.order.model.Order;
import com.service.order.model.OrderLineItem;
import com.service.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    private final KafkaTemplate<String, PlacedOrderEvent> kafkaTemplate;

    public String placeOrder(OrderRequest request) {

        List<OrderLineItem> orderLineItems = request.getOrderLineItems().stream().map(this::mapToDto).collect(Collectors.toList());
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItem::getSkuCode).collect(Collectors.toList());

        // Async communication
        // Call inventory service, Place order if product is in stock
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventories",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class).block();

        boolean allProductInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (allProductInStock) {
            orderRepository.save(order);

            // Async notification
            kafkaTemplate.send("notificationTopic", new PlacedOrderEvent(order.getOrderNumber()));

            return "Placed order successful";
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItem mapToDto(OrderLineItemDto dto) {
        return OrderLineItem.builder()
                .skuCode(dto.getSkuCode())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();
    }
}

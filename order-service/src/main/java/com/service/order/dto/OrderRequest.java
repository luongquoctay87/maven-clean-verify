package com.service.order.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<OrderLineItemDto> orderLineItems;
}

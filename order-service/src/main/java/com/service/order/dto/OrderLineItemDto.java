package com.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderLineItemDto {
    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}

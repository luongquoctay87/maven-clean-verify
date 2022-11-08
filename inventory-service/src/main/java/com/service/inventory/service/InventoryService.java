package com.service.inventory.service;

import com.service.inventory.dto.InventoryResponse;
import com.service.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "INVENTORY-SERVICE")
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStockIn(List<String> skuCodes) {
        log.info("Fetching all inventory from the database");

        return inventoryRepository.findBySkuCodeIn(skuCodes).stream().map(inventory ->
                InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build()).collect(Collectors.toList());
    }
}

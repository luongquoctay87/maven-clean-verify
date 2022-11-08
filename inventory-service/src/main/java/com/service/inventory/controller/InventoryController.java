package com.service.inventory.controller;

import com.service.inventory.dto.InventoryResponse;
import com.service.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@Slf4j(topic = "INVENTORY-CONTROLLER")
@Tag(name = "Inventory Controller")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Check product is available or not")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@Parameter(description = "List of product code", required = true) @RequestParam("skuCodes") List<String> skuCodes) {
        return inventoryService.isInStockIn(skuCodes);
    }
}

package com.service.product.controller;

import com.service.product.dto.ProductRequest;
import com.service.product.dto.ProductResponse;
import com.service.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j(topic = "PRODUCT-CONTROLLER")
@Tag(name = "Product Controller")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create New Product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest request) {
        log.info("Request POST /api/v1/products");
        productService.createProduct(request);
    }

    @Operation(summary = "Get Product List")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductList() {
        log.info("Request GET /api/v1/products");
        return productService.getAllProducts();
    }
}

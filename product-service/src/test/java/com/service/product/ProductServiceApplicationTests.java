//package com.sample.product;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sample.product.dto.ProductRequest;
//import com.sample.product.repository.ProductRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.math.BigDecimal;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@Testcontainers
//@AutoConfigureMockMvc
//class ProductServiceApplicationTests {
//
//    @Container
//    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3");
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }
//
//    @Test
//    void shouldCreatedProduct() throws Exception {
//        ProductRequest request = getProduct();
//        String productJson = objectMapper.writeValueAsString(request);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(productJson))
//                .andExpect(status().isCreated());
//        Assertions.assertEquals(1, productRepository.findAll().size());
//
//    }
//
//    private ProductRequest getProduct() {
//        return ProductRequest.builder()
//                .name("Iphone 14")
//                .description("Iphone 14")
//                .price(BigDecimal.valueOf(1200))
//                .build();
//    }
//}

package com.technical_test_Cuscatlan_adga.technical_test_adga.controllers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.services.ProductService;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.ProductWrapperResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.ProductsListWrapperResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products/v1")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/get-all-products")
    public ResponseEntity<ProductsListWrapperResponse> getAllProducts(){
        ProductsListWrapperResponse response= productService.getALlProducts();
        return ResponseEntity.status(response.getResponseAdvisor().getErrorCode()).body(response);

    }

    @GetMapping("/find-product-by-id/{id}")
    public ProductWrapperResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

}

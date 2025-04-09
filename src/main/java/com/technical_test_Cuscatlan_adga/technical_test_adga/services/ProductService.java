package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.product.Product;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.ProductWrapperResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.ProductsListWrapperResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    private final WebClient webClient;

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ProductsListWrapperResponse getALlProducts () {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        List<Product> productList = null;

        try{
            Flux<Product> productFlux = webClient.get()
                    .uri("/products")
                    .retrieve()
                    .bodyToFlux(Product.class);
            productList = productFlux.collectList().block();
        }catch(Exception ex){
            log.error(ex.getMessage());
            advisor.setErrorCode(500);
            advisor.setStatusError(HttpStatus.INTERNAL_SERVER_ERROR.name());
            advisor.setMessage(ex.getMessage());
        }

        advisor.setErrorMessages(Collections.singletonList("List of Products Obtained"));

        return new ProductsListWrapperResponse(productList,advisor);
    }


    public ProductWrapperResponse getProductById (Long id) {
        Product product = null;
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        try {
            product = webClient.get()
                    .uri("/products/" + id)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, response -> Mono.empty())
                    .bodyToMono(Product.class)
                    .block();

        } catch (Exception e) {
            advisor = new ResponseAdvisor(500, "Error");
            advisor.setMessage("Error fetching product: " + e.getMessage());
            return new ProductWrapperResponse(null, advisor);
        }

        if (product == null) {
            advisor = new ResponseAdvisor(404, "Not Found");
            advisor.setMessage("Product with ID " + id + " does not exist.");
        }

        advisor.setErrorMessages(Collections.singletonList("Search Completed"));

        return new ProductWrapperResponse(product, advisor);
    }
}

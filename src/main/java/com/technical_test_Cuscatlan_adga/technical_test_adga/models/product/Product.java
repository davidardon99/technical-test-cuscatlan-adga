package com.technical_test_Cuscatlan_adga.technical_test_adga.models.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    Long id;
    String title;
    Double price;
    String description;
    String category;
    String image;
    ProductRating rating;
}

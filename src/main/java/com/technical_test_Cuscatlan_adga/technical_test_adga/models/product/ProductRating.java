package com.technical_test_Cuscatlan_adga.technical_test_adga.models.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRating {
    Float rate;
    Integer count;
}

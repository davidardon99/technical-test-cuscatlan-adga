package com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.product.Product;
import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductWrapperResponse {
    Product product;
    ResponseAdvisor responseAdvisor;
}

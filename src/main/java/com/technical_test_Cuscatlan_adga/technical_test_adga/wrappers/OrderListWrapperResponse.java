package com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListWrapperResponse {
    private List<OrderDto> orderDtoList;
    private ResponseAdvisor responseAdvisor;
}

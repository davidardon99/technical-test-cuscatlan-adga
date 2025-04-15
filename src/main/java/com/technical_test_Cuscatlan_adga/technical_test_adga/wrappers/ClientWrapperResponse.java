package com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientWrapperResponse {
    ClientDto clientDto;
    ResponseAdvisor responseAdvisor;
}

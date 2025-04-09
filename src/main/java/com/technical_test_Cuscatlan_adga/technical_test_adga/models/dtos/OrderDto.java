package com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.OrderStatus;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;

    @NotNull(message = "Client information is required")
    @Valid
    private ClientDto client;

    @NotNull(message = "Order details are required")
    @Size(min = 1, message = "At least one order detail is required")
    @Valid
    private List<OrderDetailDto> orderDetails;

    private Double totalAmount;

    private OrderStatus status;
    private Boolean active;
    private Status orderStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

package com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private Double unitPrice;
    private Boolean active;

    private Status orderDetailStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.technical_test_Cuscatlan_adga.technical_test_adga.models.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_order_detail")
public class OrderDetail {

    @Id
    private UUID id;

    private Long productoId;
    private Integer amount;
    private Double unitPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    private Status orderDetailStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


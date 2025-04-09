package com.technical_test_Cuscatlan_adga.technical_test_adga.models.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}


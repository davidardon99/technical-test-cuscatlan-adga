package com.technical_test_Cuscatlan_adga.technical_test_adga.models.order;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.client.Client;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_orders")
public class Order {

    @Id
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;
}



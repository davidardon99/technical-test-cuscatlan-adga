package com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}

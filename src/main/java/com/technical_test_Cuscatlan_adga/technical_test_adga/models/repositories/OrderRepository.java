package com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.active = true")
    List<Order> findAllActive();

    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.active = true")
    Optional<Order> findActiveById(@Param("id") UUID id);
}

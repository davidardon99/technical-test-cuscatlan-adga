package com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories;

import com.technical_test_Cuscatlan_adga.technical_test_adga.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername (String username);
}

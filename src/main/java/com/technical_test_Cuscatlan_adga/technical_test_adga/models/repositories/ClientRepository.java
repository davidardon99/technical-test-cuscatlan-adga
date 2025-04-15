package com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query("SELECT c FROM Client c WHERE c.id = :id AND c.active = true")
    Optional<Client> findClientById (@Param("id") UUID id);

    Optional<Client> findByIdentificationNumber(String identificationNumber);
}
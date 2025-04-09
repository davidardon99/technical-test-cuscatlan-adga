package com.technical_test_Cuscatlan_adga.technical_test_adga.models.client;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_client")
public class Client {
    @Id
    private UUID id;

    private String name;
    private String lastName;
    private String identificationNumber;
    private Date birthday;
    private Integer phoneNumber;
    private String email;
    private Boolean active;
}
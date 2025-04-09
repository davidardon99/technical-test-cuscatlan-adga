package com.technical_test_Cuscatlan_adga.technical_test_adga.models.client;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.GenderType;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    private Status clientStatus;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
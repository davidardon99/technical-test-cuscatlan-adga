package com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.GenderType;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    UUID id;

    @NotBlank(message = "Name is required.")
    String name;

    @NotBlank(message = "Last Name is required.")
    String lastName;

    @NotBlank(message = "The identification number is required.")
    String identificationNumber;

    @NotNull(message = "Birthday is required")
    private Date birthday;

    @NotBlank(message = "Phone number is required")
    Integer phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Active status is required")
    private Boolean active;

    private Status clientStatus;
    private GenderType gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

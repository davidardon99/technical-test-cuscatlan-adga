package com.technical_test_Cuscatlan_adga.technical_test_adga.advisors;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAdvisor {
    private int errorCode;
    private String statusError;
    private List<String> errorMessages = new ArrayList<>();

    public ResponseAdvisor(int errorCode, String statusError) {
        this.errorCode = errorCode;
        this.statusError = statusError;
    }

    public void setMessage(String message) {
        this.errorMessages.add(message);
    }
}

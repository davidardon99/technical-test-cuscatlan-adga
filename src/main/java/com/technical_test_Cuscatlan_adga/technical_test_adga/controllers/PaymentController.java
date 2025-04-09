package com.technical_test_Cuscatlan_adga.technical_test_adga.controllers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.services.PaymentService;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.PaymentWrapperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process-order-payment/{orderId}")
    public ResponseEntity<PaymentWrapperResponse> processPayment(@PathVariable UUID orderId) {
        PaymentWrapperResponse response = paymentService.processPayment(orderId);
        return ResponseEntity.status(response.getResponseAdvisor().getErrorCode()).body(response);
    }
}

package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.OrderStatus;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.PaymentStatus;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.OrderRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.PaymentWrapperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final Random random = new Random();

    public PaymentWrapperResponse processPayment(UUID orderId) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        Optional<Order> optionalOrder = orderRepository.findActiveById(orderId);

        if (optionalOrder.isEmpty()) {
            advisor.setErrorCode(404);
            advisor.setStatusError("NOT_FOUND");
            advisor.setMessage("Order not found for ID: " + orderId);

            return PaymentWrapperResponse.builder()
                    .paymentStatus(PaymentStatus.DECLINED)
                    .responseAdvisor(advisor)
                    .build();
        }

        Order order = optionalOrder.get();
        PaymentStatus status = simulatePaymentStatus();

        if (status == PaymentStatus.APPROVED) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order); // Persist status update
        }

        advisor.setMessage("Payment result: " + status.name());

        return PaymentWrapperResponse.builder()
                .paymentStatus(status)
                .responseAdvisor(advisor)
                .build();
    }

    private PaymentStatus simulatePaymentStatus() {
        int pick = random.nextInt(3);
        return switch (pick) {
            case 0 -> PaymentStatus.APPROVED;
            case 1 -> PaymentStatus.DECLINED;
            default -> PaymentStatus.PENDING;
        };
    }
}

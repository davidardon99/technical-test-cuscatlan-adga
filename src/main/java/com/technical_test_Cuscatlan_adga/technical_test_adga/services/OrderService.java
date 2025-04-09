package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.client.Client;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.OrderDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.OrderStatus;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.OrderDetail;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.OrderRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.OrderWrapperResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderDto orderDTO, ResponseAdvisor advisor) {
        UUID orderId = UUID.randomUUID();

        // Build Client from DTO
        Client client = Client.builder()
                .id(orderDTO.getClient().getId())
                .name(orderDTO.getClient().getName())
                .lastName(orderDTO.getClient().getLastName())
                .identificationNumber(orderDTO.getClient().getIdentificationNumber())
                .birthday(orderDTO.getClient().getBirthday())
                .phoneNumber(orderDTO.getClient().getPhoneNumber())
                .email(orderDTO.getClient().getEmail())
                .active(orderDTO.getClient().getActive())
                .build();

        // Map OrderDetailDTO to OrderDetail
        List<OrderDetail> orderDetails = orderDTO.getOrderDetails().stream().map(detailDTO -> {
            OrderDetail detail = OrderDetail.builder()
                    .id(UUID.randomUUID())
                    .productoId(detailDTO.getProductId())
                    .amount(detailDTO.getAmount())
                    .unitPrice(detailDTO.getUnitPrice())
                    .build();
            return detail;
        }).collect(Collectors.toList());

        double total = orderDetails.stream()
                .mapToDouble(d -> d.getAmount() * d.getUnitPrice())
                .sum();

        Order order = Order.builder()
                .id(orderId)
                .client(client)
                .orderDetails(orderDetails)
                .totalAmount(total)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        // Set bidirectional relationship
        orderDetails.forEach(d -> d.setOrder(order));

        // Save order and cascade client/details
        Order savedOrder = orderRepository.save(order);

        advisor.setMessage("Order created successfully");
        return savedOrder;
    }

    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    @Transactional
    public Pair<OrderWrapperResponse, ResponseAdvisor> deleteOrder (UUID id){

        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        OrderWrapperResponse orderWrapperResponse = new OrderWrapperResponse();

        try {
            if(!orderRepository.existsById(id))throw new Exception("La orden no existe");
            orderRepository.deleteById(id);
        }catch (Exception ex){
            log.error(ex.getMessage());
            advisor.setErrorCode(400);
            advisor.setStatusError(HttpStatus.BAD_REQUEST.name());
            advisor.setMessage(ex.getMessage());
        }
        return Pair.of( orderWrapperResponse, advisor);
    }
}

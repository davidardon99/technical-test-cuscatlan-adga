package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.client.Client;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.OrderDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.OrderStatus;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.OrderDetail;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.OrderRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.OrderListWrapperResponse;
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
        LocalDateTime now = LocalDateTime.now();

        // Build Client
        Client client = Client.builder()
                .id(orderDTO.getClient().getId())
                .name(orderDTO.getClient().getName())
                .lastName(orderDTO.getClient().getLastName())
                .identificationNumber(orderDTO.getClient().getIdentificationNumber())
                .birthday(orderDTO.getClient().getBirthday())
                .phoneNumber(orderDTO.getClient().getPhoneNumber())
                .email(orderDTO.getClient().getEmail())
                .active(orderDTO.getClient().getActive())
                .gender(orderDTO.getClient().getGender())
                .clientStatus(Status.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Build OrderDetails
        List<OrderDetail> orderDetails = orderDTO.getOrderDetails().stream().map(detailDTO -> {
            return OrderDetail.builder()
                    .id(UUID.randomUUID())
                    .productoId(detailDTO.getProductId())
                    .amount(detailDTO.getAmount())
                    .unitPrice(detailDTO.getUnitPrice())
                    .active(detailDTO.getActive() != null ? detailDTO.getActive() : true)
                    .orderDetailStatus(Status.CREATED)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        }).collect(Collectors.toList());

        double total = orderDetails.stream()
                .mapToDouble(d -> d.getAmount() * d.getUnitPrice())
                .sum();

        // Build Order
        Order order = Order.builder()
                .id(orderId)
                .client(client)
                .orderDetails(orderDetails)
                .totalAmount(total)
                .status(OrderStatus.CREATED)
                .orderStatus(Status.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .active(orderDTO.getActive() != null ? orderDTO.getActive() : true)
                .build();

        // Set bidirectional link
        orderDetails.forEach(d -> d.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        advisor.setMessage("Order created successfully");
        return savedOrder;
    }


    public Order updateOrderById(UUID orderId, OrderDto orderDTO, ResponseAdvisor advisor) {
        Optional<Order> optionalOrder = orderRepository.findActiveById(orderId);

        if (optionalOrder.isEmpty()) {
            advisor.setErrorCode(404);
            advisor.setStatusError("NOT_FOUND");
            advisor.setMessage("Order not found with ID: " + orderId);
            return null;
        }

        Order existingOrder = optionalOrder.get();
        LocalDateTime now = LocalDateTime.now();

        //UPDATE EXISTING CLIENT (without creating a new one)
        Client existingClient = existingOrder.getClient();

        existingClient.setName(orderDTO.getClient().getName());
        existingClient.setLastName(orderDTO.getClient().getLastName());
        existingClient.setIdentificationNumber(orderDTO.getClient().getIdentificationNumber());
        existingClient.setBirthday(orderDTO.getClient().getBirthday());
        existingClient.setPhoneNumber(orderDTO.getClient().getPhoneNumber());
        existingClient.setEmail(orderDTO.getClient().getEmail());
        existingClient.setGender(orderDTO.getClient().getGender());
        existingClient.setActive(orderDTO.getClient().getActive() != null ? orderDTO.getClient().getActive() : true);
        existingClient.setClientStatus(Status.UPDATED);
        existingClient.setUpdatedAt(now);

        //Handling details: marking existing as inactive
        List<OrderDetail> existingDetails = existingOrder.getOrderDetails().stream()
                .filter(OrderDetail::getActive)
                .collect(Collectors.toList());
        existingDetails.forEach(detail -> {
            detail.setActive(false);
            detail.setOrderDetailStatus(Status.DELETED);
            detail.setUpdatedAt(now);
        });

        //Add new details
        List<OrderDetail> incomingDetails = orderDTO.getOrderDetails().stream()
                .map(detailDTO -> OrderDetail.builder()
                        .id(UUID.randomUUID())
                        .productoId(detailDTO.getProductId())
                        .amount(detailDTO.getAmount())
                        .unitPrice(detailDTO.getUnitPrice())
                        .order(existingOrder)
                        .active(detailDTO.getActive() != null ? detailDTO.getActive() : true)
                        .orderDetailStatus(Status.UPDATED)
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .collect(Collectors.toList());

        // Calculate total
        double total = incomingDetails.stream()
                .filter(OrderDetail::getActive)
                .mapToDouble(d -> d.getAmount() * d.getUnitPrice())
                .sum();

        // Update order
        existingOrder.getOrderDetails().clear();
        existingOrder.getOrderDetails().addAll(incomingDetails);
        existingOrder.setTotalAmount(total);
        existingOrder.setStatus(orderDTO.getStatus() != null ? orderDTO.getStatus() : OrderStatus.CREATED);
        existingOrder.setOrderStatus(Status.UPDATED);
        existingOrder.setUpdatedAt(now);
        existingOrder.setActive(orderDTO.getActive() != null ? orderDTO.getActive() : true);

        Order updatedOrder = orderRepository.save(existingOrder);
        advisor.setMessage("Order updated successfully");
        return updatedOrder;
    }


    @Transactional
    public Pair<OrderWrapperResponse, ResponseAdvisor> deleteOrder(UUID id) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        OrderWrapperResponse wrapper = new OrderWrapperResponse();

        try {
            Optional<Order> optionalOrder = orderRepository.findActiveById(id);
            if (optionalOrder.isEmpty()) {
                throw new Exception("Order not found or already inactive");
            }

            Order order = optionalOrder.get();
            LocalDateTime now = LocalDateTime.now();

            // Soft delete
            order.setActive(false);
            order.setOrderStatus(Status.DELETED);
            order.setUpdatedAt(now);

            order.getOrderDetails().forEach(detail -> {
                detail.setActive(false);
                detail.setOrderDetailStatus(Status.DELETED);
                detail.setUpdatedAt(now);
            });

            order.getClient().setActive(false);
            order.getClient().setClientStatus(Status.DELETED);
            order.getClient().setUpdatedAt(now);

            orderRepository.save(order);

            advisor.setMessage("Order marked as inactive (soft deleted)");
            wrapper.setOrder(order);
            wrapper.setResponseAdvisor(advisor);

        } catch (Exception e) {
            log.error(e.getMessage());
            advisor.setErrorCode(400);
            advisor.setStatusError(HttpStatus.BAD_REQUEST.name());
            advisor.setMessage(e.getMessage());
            wrapper.setOrder(null);
            wrapper.setResponseAdvisor(advisor);
        }

        return Pair.of(wrapper, advisor);
    }



    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findActiveById(id);
    }

    public OrderListWrapperResponse getAllOrders () {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        List<Order> orders = orderRepository.findAllActive();

        if (orders.isEmpty()) {
            advisor.setMessage("No orders found");
        } else {
            advisor.setMessage("Orders retrieved successfully");
        }

        return OrderListWrapperResponse.builder()
                .orders(orders)
                .responseAdvisor(advisor)
                .build();
    }
}

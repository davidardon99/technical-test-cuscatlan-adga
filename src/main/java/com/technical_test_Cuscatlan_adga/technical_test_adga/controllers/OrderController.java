package com.technical_test_Cuscatlan_adga.technical_test_adga.controllers;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.ClientDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.OrderDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.OrderRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.services.OrderService;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.ClientWrapperResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.OrderDtoWrapperResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.OrderListWrapperResponse;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.OrderWrapperResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/create-order")
    public ResponseEntity<OrderWrapperResponse> createOrder(@RequestBody @Valid OrderDto orderDTO) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        Order order = orderService.createOrder(orderDTO, advisor);

        if (order == null) {
            return ResponseEntity
                    .status(advisor.getErrorCode())
                    .body(OrderWrapperResponse.builder()
                            .order(null)
                            .responseAdvisor(advisor)
                            .build());
        }

        return ResponseEntity.ok(OrderWrapperResponse.builder()
                .order(order)
                .responseAdvisor(advisor)
                .build());
    }


    @PutMapping("/update-order-by-id/{id}")
    public ResponseEntity<OrderWrapperResponse> updateOrderById(
            @PathVariable UUID id,
            @Valid @RequestBody OrderDto orderDTO) {

        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        Order updatedOrder = orderService.updateOrderById(id, orderDTO, advisor);

        OrderWrapperResponse response = OrderWrapperResponse.builder()
                .order(updatedOrder)
                .responseAdvisor(advisor)
                .build();

        HttpStatus status = advisor.getErrorCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/delete-order-by-id/{id}")
    public ResponseEntity<OrderWrapperResponse> deleteOrderById (@PathVariable("id") UUID id){

        var orders = orderService.deleteOrder(id);
        HttpStatus status = (orders.getSecond().getStatusError().equals("SUCCESS"))? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new OrderWrapperResponse(orders.getFirst().getOrder(), orders.getSecond()),status);
    }

    @GetMapping("/find-order-by-id/{id}")
    public ResponseEntity<OrderDtoWrapperResponse> getOrderByIdDto(@PathVariable UUID id) {
        OrderDtoWrapperResponse response = orderService.getOrderByIdDto(id);

        if (response.getResponseAdvisor().getErrorCode() != 200) {
            return ResponseEntity.status(response.getResponseAdvisor().getErrorCode())
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<OrderListWrapperResponse> getAllOrdersDto() {
        OrderListWrapperResponse response = orderService.getAllOrdersDto();
        return ResponseEntity.status(response.getResponseAdvisor().getErrorCode())
                .body(response);
    }


    @PostMapping("/create-client")
    public ResponseEntity<ClientWrapperResponse> createClient(@RequestBody @Valid ClientDto clientDTO) {
        ClientWrapperResponse response = orderService.createClient(clientDTO);
        HttpStatus status = (response.getResponseAdvisor().getErrorCode() == 200) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/update-client-by-id/{id}")
    public ResponseEntity<ClientWrapperResponse> updateClient(
            @PathVariable("id") UUID id,
            @RequestBody @Valid ClientDto clientDTO) {

        ClientWrapperResponse response = orderService.updateClient(id, clientDTO);
        HttpStatus status = (response.getResponseAdvisor().getErrorCode() == 200) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}

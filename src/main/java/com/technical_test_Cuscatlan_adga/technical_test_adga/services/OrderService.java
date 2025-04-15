package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.advisors.ResponseAdvisor;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.client.Client;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.ClientDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.OrderDetailDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.dtos.OrderDto;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.OrderStatus;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.enums.Status;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.Order;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.order.OrderDetail;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.ClientRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.OrderRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.wrappers.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ClientRepository clientRepository;

    private final ProductService productService;

    public Order createOrder (OrderDto orderDTO, ResponseAdvisor advisor) {
        UUID orderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Validation: empty list
        if (orderDTO.getOrderDetails() == null || orderDTO.getOrderDetails().isEmpty()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Order must contain at least one product.");
            return null;
        }

        // Client validations
        ResponseAdvisor clientValidation = validateClient(orderDTO.getClient());
        if (clientValidation.getErrorCode() != 200) {
            advisor.setErrorCode(clientValidation.getErrorCode());
            advisor.setStatusError(clientValidation.getStatusError());
            advisor.setErrorMessages(clientValidation.getErrorMessages());
            return null;
        }


        // Construction of order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailDto detailDTO : orderDTO.getOrderDetails()) {
            if (detailDTO.getUnitPrice() == null || detailDTO.getUnitPrice() <= 0) {
                advisor.setErrorCode(400);
                advisor.setStatusError("BAD_REQUEST");
                advisor.setMessage("Each product must have a positive unit price.");
                return null;
            }

            // Validate product existence in external API
            ProductWrapperResponse productResponse = productService.getProductById(detailDTO.getProductId());
            if (productResponse.getProduct() == null) {
                advisor.setErrorCode(404);
                advisor.setStatusError("NOT_FOUND");
                advisor.setMessage("Product with ID " + detailDTO.getProductId() + " does not exist in the external API.");
                return null;
            }

            orderDetails.add(OrderDetail.builder()
                    .id(UUID.randomUUID())
                    .productoId(detailDTO.getProductId())
                    .amount(detailDTO.getAmount())
                    .unitPrice(detailDTO.getUnitPrice())
                    .active(detailDTO.getActive() != null ? detailDTO.getActive() : true)
                    .orderDetailStatus(Status.CREATED)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        // Calculate total
        double total = orderDetails.stream()
                .mapToDouble(d -> d.getAmount() * d.getUnitPrice())
                .sum();

        if (total <= 0) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Total order amount must be greater than zero.");
            return null;
        }

        // Client Construction
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

        // Construction of the order
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

        orderDetails.forEach(d -> d.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        advisor.setMessage("Order created successfully");
        return savedOrder;
    }

    public Order updateOrderById (UUID orderId, OrderDto orderDTO, ResponseAdvisor advisor) {
        Optional<Order> optionalOrder = orderRepository.findActiveById(orderId);

        if (optionalOrder.isEmpty()) {
            advisor.setErrorCode(404);
            advisor.setStatusError("NOT_FOUND");
            advisor.setMessage("Order not found with ID: " + orderId);
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        Order existingOrder = optionalOrder.get();
        Client existingClient = existingOrder.getClient();

        // Validate if the same identificationNumber is in a different client
        ClientDto clientDto = orderDTO.getClient();
        if (clientDto != null) {
            clientDto.setId(existingClient.getId());
            ResponseAdvisor clientValidation = validateClient(clientDto);
            if (clientValidation.getErrorCode() != 200) {
                advisor.setErrorCode(clientValidation.getErrorCode());
                advisor.setStatusError(clientValidation.getStatusError());
                advisor.setErrorMessages(clientValidation.getErrorMessages());
                return null;
            }
        }

        // validation: empty list
        if (orderDTO.getOrderDetails() == null || orderDTO.getOrderDetails().isEmpty()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Order must contain at least one product.");
            return null;
        }

        List<OrderDetail> orderDetails = new ArrayList<>();
        boolean invalidUnitPrice = false;
        boolean invalidProduct = false;

        for (OrderDetailDto detailDTO : orderDTO.getOrderDetails()) {
            if (detailDTO.getUnitPrice() == null || detailDTO.getUnitPrice() <= 0) {
                invalidUnitPrice = true;
                advisor.setErrorCode(400);
                advisor.setStatusError("BAD_REQUEST");
                advisor.setMessage("Each product must have a positive unit price.");
                break;
            }

            try {
                ProductWrapperResponse productResp = productService.getProductById(detailDTO.getProductId());
                if (productResp.getProduct() == null) {
                    invalidProduct = true;
                    advisor.setErrorCode(400);
                    advisor.setStatusError("BAD_REQUEST");
                    advisor.setMessage("Product with ID " + detailDTO.getProductId() + " does not exist in external API.");
                    break;
                }
            } catch (Exception e) {
                invalidProduct = true;
                advisor.setErrorCode(500);
                advisor.setStatusError("ERROR");
                advisor.setMessage("Error verifying product with ID: " + detailDTO.getProductId());
                break;
            }

            orderDetails.add(OrderDetail.builder()
                    .id(UUID.randomUUID())
                    .productoId(detailDTO.getProductId())
                    .amount(detailDTO.getAmount())
                    .unitPrice(detailDTO.getUnitPrice())
                    .active(detailDTO.getActive() != null ? detailDTO.getActive() : true)
                    .orderDetailStatus(Status.UPDATED)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        if (invalidUnitPrice || invalidProduct) {
            return null;
        }

        double total = orderDetails.stream()
                .mapToDouble(d -> d.getAmount() * d.getUnitPrice())
                .sum();

        // Update Client
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

        List<OrderDetail> existingDetails = existingOrder.getOrderDetails().stream()
                .filter(OrderDetail::getActive)
                .collect(Collectors.toList());
        existingDetails.forEach(detail -> {
            detail.setActive(false);
            detail.setOrderDetailStatus(Status.DELETED);
            detail.setUpdatedAt(now);
        });

        existingOrder.getOrderDetails().clear();
        existingOrder.getOrderDetails().addAll(orderDetails);
        existingOrder.setTotalAmount(total);
        existingOrder.setStatus(orderDTO.getStatus() != null ? orderDTO.getStatus() : OrderStatus.CREATED);
        existingOrder.setOrderStatus(Status.UPDATED);
        existingOrder.setUpdatedAt(now);
        existingOrder.setActive(orderDTO.getActive() != null ? orderDTO.getActive() : true);

        orderDetails.forEach(d -> d.setOrder(existingOrder));

        Order updatedOrder = orderRepository.save(existingOrder);
        advisor.setMessage("Order updated successfully");
        return updatedOrder;
    }

    @Transactional
    public Pair<OrderWrapperResponse, ResponseAdvisor> deleteOrder (UUID id) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        OrderWrapperResponse wrapper = new OrderWrapperResponse();

        try {
            Optional<Order> optionalOrder = orderRepository.findActiveById(id);
            if (optionalOrder.isEmpty()) {
                throw new Exception("Order not found or already inactive");
            }

            Order order = optionalOrder.get();
            LocalDateTime now = LocalDateTime.now();

            //Delete
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

            advisor.setMessage("Order marked as inactive (Logic Delete)");
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

    private OrderDto mapToDto (Order order) {
        ClientDto clientDto = ClientDto.builder()
                .id(order.getClient().getId())
                .name(order.getClient().getName())
                .lastName(order.getClient().getLastName())
                .identificationNumber(order.getClient().getIdentificationNumber())
                .birthday(order.getClient().getBirthday())
                .phoneNumber(order.getClient().getPhoneNumber())
                .email(order.getClient().getEmail())
                .active(order.getClient().getActive())
                .gender(order.getClient().getGender())
                .clientStatus(order.getClient().getClientStatus())
                .createdAt(order.getClient().getCreatedAt())
                .updatedAt(order.getClient().getUpdatedAt())
                .build();

        List<OrderDetailDto> detailDtos = order.getOrderDetails().stream()
                .map(detail -> {
                    String productName = "";
                    try {
                        ProductWrapperResponse productResp = productService.getProductById(detail.getProductoId());
                        if (productResp.getProduct() != null) {
                            productName = productResp.getProduct().getTitle();
                        }
                    } catch (Exception e) {
                        productName = "(Name unavailable)";
                    }

                    return OrderDetailDto.builder()
                            .productId(detail.getProductoId())
                            .amount(detail.getAmount())
                            .unitPrice(detail.getUnitPrice())
                            .active(detail.getActive())
                            .orderDetailStatus(detail.getOrderDetailStatus())
                            .createdAt(detail.getCreatedAt())
                            .updatedAt(detail.getUpdatedAt())
                            .productName(productName)
                            .build();
                }).collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .client(clientDto)
                .orderDetails(detailDtos)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .active(order.getActive())
                .build();
    }

    public OrderDtoWrapperResponse getOrderByIdDto (UUID id) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        if (id == null) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Order ID must not be null.");
            return OrderDtoWrapperResponse.builder()
                    .orderDto(null)
                    .responseAdvisor(advisor)
                    .build();
        }

        Optional<Order> optionalOrder = orderRepository.findActiveById(id);

        if (optionalOrder.isEmpty()) {
            advisor.setErrorCode(404);
            advisor.setStatusError("NOT_FOUND");
            advisor.setMessage("Order not found with ID: " + id);
            return OrderDtoWrapperResponse.builder()
                    .orderDto(null)
                    .responseAdvisor(advisor)
                    .build();
        }

        Order order = optionalOrder.get();
        OrderDto orderDto = mapToDto(order);

        advisor.setMessage("Order retrieved successfully");
        return OrderDtoWrapperResponse.builder()
                .orderDto(orderDto)
                .responseAdvisor(advisor)
                .build();
    }

    public OrderListWrapperResponse getAllOrdersDto () {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");
        List<Order> orders = orderRepository.findAllActive();

        List<OrderDto> orderDtos = orders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        if (orderDtos.isEmpty()) {
            advisor.setMessage("No orders found");
        } else {
            advisor.setMessage("Orders retrieved successfully");
        }

        return OrderListWrapperResponse.builder()
                .orderDtoList(orderDtos)
                .responseAdvisor(advisor)
                .build();
    }

    //New service for create only a new client:
    private ResponseAdvisor validateClient(ClientDto clientDto) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        if (clientDto.getName() == null || clientDto.getName().isBlank()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client name is required.");
            return advisor;
        }

        if (clientDto.getLastName() == null || clientDto.getLastName().isBlank()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client last name is required.");
            return advisor;
        }

        if (clientDto.getIdentificationNumber() == null || clientDto.getIdentificationNumber().length() != 13) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Identification number must be 13 digits.");
            return advisor;
        }

        if (clientDto.getPhoneNumber() == null || String.valueOf(clientDto.getPhoneNumber()).length() != 8) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Phone number must be 8 digits.");
            return advisor;
        }

        if (clientDto.getEmail() == null || !clientDto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Invalid email format.");
            return advisor;
        }

        if (clientDto.getBirthday().after(new Date())) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Birthday cannot be in the future.");
            return advisor;
        }

        // Validate if the same identificationNumber is in a different client
        boolean exists = clientRepository.findAll().stream()
                .anyMatch(c -> c.getIdentificationNumber().equals(clientDto.getIdentificationNumber())
                        && Boolean.TRUE.equals(c.getActive())
                        && !c.getId().equals(clientDto.getId())); // <- permitimos si es el mismo ID

        if (exists) {
            advisor.setErrorCode(409);
            advisor.setStatusError("CONFLICT");
            advisor.setMessage("A client with the same identification number already exists.");
            return advisor;
        }

        return advisor;
    }

    public ClientWrapperResponse createClient (ClientDto clientDto) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        if (clientDto.getId() == null) {
            clientDto.setId(UUID.randomUUID());
        }

        // validations
        if (clientDto.getId() == null) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client ID is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getName() == null || clientDto.getName().isBlank()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client name is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getLastName() == null || clientDto.getLastName().isBlank()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client last name is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getIdentificationNumber() == null || clientDto.getIdentificationNumber().length() != 13) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Identification number must be 13 digits.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getPhoneNumber() == null || String.valueOf(clientDto.getPhoneNumber()).length() != 8) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Phone number must be 8 digits.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getEmail() == null || !clientDto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Invalid email format.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getBirthday().after(new Date())) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Birthday is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        boolean exists = clientRepository.findAll().stream()
                .anyMatch(c -> c.getIdentificationNumber().equals(clientDto.getIdentificationNumber()) && Boolean.TRUE.equals(c.getActive()));

        if (exists) {
            advisor.setErrorCode(409);
            advisor.setStatusError("CONFLICT");
            advisor.setMessage("A client with the same identification number already exists.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        LocalDateTime now = LocalDateTime.now();

        Client client = Client.builder()
                .id(clientDto.getId())
                .name(clientDto.getName())
                .lastName(clientDto.getLastName())
                .identificationNumber(clientDto.getIdentificationNumber())
                .birthday(clientDto.getBirthday())
                .phoneNumber(clientDto.getPhoneNumber())
                .email(clientDto.getEmail())
                .active(clientDto.getActive() != null ? clientDto.getActive() : true)
                .gender(clientDto.getGender())
                .clientStatus(Status.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        clientRepository.save(client);

        ClientDto responseDto = ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .lastName(client.getLastName())
                .identificationNumber(client.getIdentificationNumber())
                .birthday(client.getBirthday())
                .phoneNumber(client.getPhoneNumber())
                .email(client.getEmail())
                .active(client.getActive())
                .gender(client.getGender())
                .clientStatus(client.getClientStatus())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();

        advisor.setMessage("Client created successfully.");
        return ClientWrapperResponse.builder().clientDto(responseDto).responseAdvisor(advisor).build();
    }

    public ClientWrapperResponse updateClient (UUID clientId, ClientDto clientDto) {
        ResponseAdvisor advisor = new ResponseAdvisor(200, "SUCCESS");

        // validate that the client exists
        Optional<Client> optionalClient = clientRepository.findClientById(clientId);
        if (optionalClient.isEmpty()) {
            advisor.setErrorCode(404);
            advisor.setStatusError("NOT_FOUND");
            advisor.setMessage("Client not found with ID: " + clientId);
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getName() == null || clientDto.getName().isBlank()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client name is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getLastName() == null || clientDto.getLastName().isBlank()) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Client last name is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getIdentificationNumber() == null || clientDto.getIdentificationNumber().length() > 13) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Identification number must be 13 digits or fewer.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getPhoneNumber() == null || String.valueOf(clientDto.getPhoneNumber()).length() > 8) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Phone number must be 8 digits or fewer.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getEmail() == null || !clientDto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Invalid email format.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        if (clientDto.getBirthday() == null) {
            advisor.setErrorCode(400);
            advisor.setStatusError("BAD_REQUEST");
            advisor.setMessage("Birthday is required.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        // Duplicate ID Validation
        boolean duplicateIdNumber = clientRepository.findAll().stream()
                .anyMatch(c -> !c.getId().equals(clientId) &&
                        c.getIdentificationNumber().equals(clientDto.getIdentificationNumber()) &&
                        Boolean.TRUE.equals(c.getActive()));

        if (duplicateIdNumber) {
            advisor.setErrorCode(409);
            advisor.setStatusError("CONFLICT");
            advisor.setMessage("A client with the same identification number already exists.");
            return ClientWrapperResponse.builder().clientDto(null).responseAdvisor(advisor).build();
        }

        // Update Client
        Client existing = optionalClient.get();

        existing.setName(clientDto.getName());
        existing.setLastName(clientDto.getLastName());
        existing.setIdentificationNumber(clientDto.getIdentificationNumber());
        existing.setBirthday(clientDto.getBirthday());
        existing.setPhoneNumber(clientDto.getPhoneNumber());
        existing.setEmail(clientDto.getEmail());
        existing.setGender(clientDto.getGender());
        existing.setActive(clientDto.getActive() != null ? clientDto.getActive() : true);
        existing.setClientStatus(Status.UPDATED);
        existing.setUpdatedAt(LocalDateTime.now());

        clientRepository.save(existing);

        ClientDto responseDto = ClientDto.builder()
                .id(existing.getId())
                .name(existing.getName())
                .lastName(existing.getLastName())
                .identificationNumber(existing.getIdentificationNumber())
                .birthday(existing.getBirthday())
                .phoneNumber(existing.getPhoneNumber())
                .email(existing.getEmail())
                .active(existing.getActive())
                .gender(existing.getGender())
                .clientStatus(existing.getClientStatus())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();

        advisor.setMessage("Client updated successfully.");
        return ClientWrapperResponse.builder()
                .clientDto(responseDto)
                .responseAdvisor(advisor)
                .build();
    }
}

# Technical Test - Cuscatlan - adga

## ✨ Project Summary
This is a microservice-based shopping cart backend application developed with **Java** and **Spring Boot**. It includes the following components:

- Product API (proxy to [FakeStoreAPI](https://fakestoreapi.com))
- Order API (create, view, list orders)
- Payment API (simulates payment process)
- Models and DTOs for `Client`, `Order`, and `OrderDetail`
- Unified response model using `ResponseAdvisor`
- H2 in-memory persistence with JPA repositories

---

## 📅 Project Structure
```
src/main/java/com/technical_test_Cuscatlan_adga/technical_test_adga
|
|-- models
|   |-- Product.java
|   |-- ProductRating.java
|   |-- Client.java (@Entity)
|   |-- Order.java (@Entity)
|   |-- OrderDetail.java (@Entity)
|   |-- enums: OrderStatus.java, PaymentStatus.java
|
|-- dtos
|   |-- ClientDTO.java
|   |-- OrderDTO.java
|   |-- OrderDetailDTO.java
|
|-- wrappers
|   |-- ProductWrapperResponse.java
|   |-- ProductsListWrapperResponse.java
|   |-- PaymentWrapperResponse.java
|
|-- advisors
|   |-- ResponseAdvisor.java
|
|-- config
|   |-- WebClientConfig.java
|   |-- SecurityConfig.java
|
|-- services
|   |-- ProductService.java
|   |-- OrderService.java
|   |-- PaymentService.java
|
|-- repositories
|   |-- ClientRepository.java
|   |-- OrderRepository.java
|   |-- OrderDetailRepository.java
|
|-- controllers
|   |-- ProductController.java
|   |-- OrderController.java
|   |-- PaymentController.java
```

---

## 🔍 Endpoints Summary

### ▶️ Products API
```
GET /api/products/v1/get-all-products
GET /api/products/v1/find-product-by-id/{id}
```

### ▶️ Orders API
```
POST    /api/orders/v1/create-order
GET     /api/orders/v1/find-order-by-id/{id}
GET     /api/orders/v1/get-all-orders
DELETE  /api/orders/v1/delete-order-by-id/{id}
```

### ▶️ Payments API
```
POST /api/payments/v1/process/{orderId}
```

---

## 🔮 Response Format (All APIs)
All APIs return a standard structure with:
```json
{
  "<result>": { ... },
  "responseAdvisor": {
    "errorCode": 200,
    "statusError": "SUCCESS",
    "errorMessages": ["message"]
  }
}
```

---

## 🔧 Running the Project

1. Clone the project
2. Import as Maven project in IntelliJ or any IDE
3. Set the following property in `application.properties`:
```properties
external.api.products.url=https://fakestoreapi.com
```
4. Run `TechnicalTestAdgaApplication.java`

---

## 🔔 Testing

### ✅ Manual Testing (Recommended for Now)
Use **Postman**, **Insomnia**, or **curl** to test the endpoints.

#### Sample Order POST:
```json
POST /api/orders/v1/create
{
  "client": {
    "id": "<uuid>",
    "name": "John",
    "lastName": "Doe",
    "identificationNumber": "123456789",
    "birthday": "2000-01-01",
    "phoneNumber": 77778888,
    "email": "john@example.com",
    "active": true
  },
  "orderDetails": [
    {
      "productId": 1,
      "amount": 2,
      "unitPrice": 100.0
    }
  ]
}
```

### 📊 Optional: Add H2 Database
H2 is already integrated and active with in-memory mode.

**application.properties:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

**H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- User: sa
- Password: (blank)

---

## 📚 Author
- Technical Test for Cuscatlan
- Developed by: adga
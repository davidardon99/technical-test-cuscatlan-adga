# 🛒 Technical Test - Cuscatlan - adga

This project is a microservices-based solution using **Java** and **Spring Boot**, simulating a **shopping cart system** with external product API integration.

## 🧩 General Architecture

The system includes the following modules:

### 🔹 Products (External API)
- Acts as a proxy to: `https://fakestoreapi.com`
- Available endpoints:
    - `GET /api/products/v1/get-all-products`
    - `GET /api/products/v1/find-product-by-id/{id}`

### 🔹 Orders
- Create, update, retrieve, and delete (soft delete).
- Full integration with Client and Order Details.

### 🔹 Payments (Simulated)
- Endpoints simulate payment statuses: `APPROVED`, `DECLINED`, `PENDING`

---
## 📌 Diagram

![Diagram](./diagram.png)

---


## 📦 Main Entities

### `Client`
```java
UUID id;
String name;
String lastName;
String identificationNumber;
Date birthday;
Integer phoneNumber;
String email;
Gender gender;
Boolean active;
Status clientStatus;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

### `Order`
```java
UUID id;
Client client;
List<OrderDetail> orderDetails;
Double totalAmount;
OrderStatus status;
Boolean active;
Status orderStatus;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

### `OrderDetail`
```java
UUID id;
Long productoId;
Integer amount;
Double unitPrice;
Order order;
Boolean active;
Status orderDetailStatus;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

### `Enum Gender`
```java
MALE, FEMALE, OTHER
```

### `Enum Status`
```java
CREATED, UPDATED, DELETED
```

### `Enum OrderStatus`
```java
CREATED, PAID, CANCELLED
```

## 📁 Project Structure

```bash
src/main/java/com/technical_test_Cuscatlan_adga/technical_test_adga
│
├── models
│   ├── Product.java
│   ├── ProductRating.java
│   ├── Client.java (@Entity)
│   ├── Order.java (@Entity)
│   ├── OrderDetail.java (@Entity)
│   └── enums: OrderStatus.java, PaymentStatus.java, Status.java, Gender.java
│
├── dtos
│   ├── ClientDTO.java
│   ├── OrderDTO.java
│   ├── OrderDetailDTO.java
│
├── wrappers
│   ├── ProductWrapperResponse.java
│   ├── ProductsListWrapperResponse.java
│   ├── PaymentWrapperResponse.java
│   └── OrderWrapperResponse.java
│
├── advisors
│   └── ResponseAdvisor.java
│
├── config
│   ├── WebClientConfig.java
│   └── SecurityConfig.java
│
├── services
│   ├── ProductService.java
│   ├── OrderService.java
│   └── PaymentService.java
│
├── repositories
│   ├── ClientRepository.java
│   ├── OrderRepository.java
│   └── OrderDetailRepository.java
│
└── controllers
    ├── ProductController.java
    ├── OrderController.java
    └── PaymentController.java
```

## 🔐 Security
- Disabled for development (using `permitAll()` in `SecurityConfig`).

## 🧪 Database (H2)
- Uses H2 in-memory database.
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`

## ✅ Available Endpoints

### 📦 Products
- `GET /api/products/v1/get-all-products`
- `GET /api/products/v1/find-product-by-id/{id}`

### 📑 Orders
- `POST /api/orders/v1/create-order`
- `PUT /api/orders/v1/update-order-by-id/{id}`
- `GET /api/orders/v1/get-order-by-id/{id}`
- `GET /api/orders/v1/get-all-orders`
- `DELETE /api/orders/v1/delete-order-by-id/{id}`

### 💳 Payments
- `POST /api/payments/v1/process-order-payment/{orderId}`

## 🔄 Delete Order
- All entities (`Order`, `OrderDetail`, `Client`) implement soft delete using `Boolean active`.
- All find operations only return active records.
- Deletion sets `active = false` and `status = DELETED`.


## 🔮 Response Format
-All APIs return a standard structure with:
```json
{
  "<result>": {...},
  "responseAdvisor": {
    "errorCode": 200,
    "statusError": "SUCCESS",
    "errorMessages": ["message"]
  }
}
```

## 🧪 You can test endpoints with **Postman** or Swagger UI at:
### 📄 Swagger:
- `http://localhost:8080/swagger-ui/index.html`

### 📄 Post Man:

### Example **Create Order** 
POST `http://localhost:8080/api/orders/v1/create-order`

```json
{
  "client": {
    "id": "d7842f91-81da-4a9a-a18d-6a5c2c1a9770",
    "name": "Angel David",
    "lastName": "González Ardón",
    "identificationNumber": "0703199905137",
    "birthday": "1999-08-26",
    "phoneNumber": 33570625,
    "email": "angelgonzalez.nl90@gmail.com",
    "gender": "MALE",
    "active": true
  },
  "orderDetails": [
    {
      "productId": 1,
      "amount": 2,
      "unitPrice": 50.0,
      "active": true
    },
    {
      "productId": 3,
      "amount": 1,
      "unitPrice": 80.0,
      "active": true
    }
  ],
  "active": true
}
```

### Example **Update Order**
PUT `http://localhost:8080/api/orders/v1/update-order-by-id/{id}`:
```json
{
  "client": {
    "id": "9e3fe921-6324-4011-b50c-ac8a988d51ca",
    "name": "David",
    "lastName": "Ardón",
    "identificationNumber": "0801199905137",
    "birthday": "1999-08-26",
    "phoneNumber": 98313233,
    "email": "angelgonzalez.nl90@gmail.com",
    "gender": "MALE",
    "active": true
  },
  "orderDetails": [
    {
      "productId": 1,
      "amount": 5,
      "unitPrice": 50.0,
      "active": true
    }
  ],
  "active": true
}
```

### Example **Delete Order**
DELETE `http://localhost:8080/delete-order-by-id/{id}`

### Example **Get All Orders**
GET `http://localhost:8080/api/orders/v1/get-all-orders`

### Example **Get Order By Id**
GET `http://localhost:8080/api/orders/v1/find-order-by-id/{id}`

---

## 🚀 How to Run the Project

1. Clone the repository
2. In `application.properties`, configure:
   ```properties
   spring.application.name=technical-test-adga
   application-description=Calling Third Party API in Spring Boot
   application-version=1.0.0
   info.app.name=fakestore-api
   info.app.description=@application-description@
   info.app.version=@application-version@

   server.port=8080

   external.api.products.url=https://fakestoreapi.com
   management.endpoints.web.exposure.include=*
   management.endpoint.health.show-details=always

   logging.level.org.springframework.web=DEBUG

   spring.security.enabled=false

   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   ```
3. Run `TechnicalTestAdgaApplication.java`
4. Access Swagger and H2 Console

---

## 👨‍💻 Author
**Angel David González Ardón**

Ready for integration and testing! 🚀


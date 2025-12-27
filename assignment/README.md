# RESTful API Development with Java and MySQL

## Project Overview
RESTful API for e-commerce order processing system built with Java Spring Boot 3.5.0 and MySQL 9.5.0.

## Database Schema

### Tables

#### 1. Customer
- customer_id (INT, PRIMARY KEY, AUTO_INCREMENT)
- first_name (VARCHAR(50), NOT NULL)
- last_name (VARCHAR(50), NOT NULL)

#### 2. Contact_Mech
- contact_mech_id (INT, PRIMARY KEY, AUTO_INCREMENT)
- customer_id (INT, NOT NULL, FOREIGN KEY)
- street_address (VARCHAR(100), NOT NULL)
- city (VARCHAR(50), NOT NULL)
- state (VARCHAR(50), NOT NULL)
- postal_code (VARCHAR(20), NOT NULL)
- phone_number (VARCHAR(20))
- email (VARCHAR(100))

#### 3. Product
- product_id (INT, PRIMARY KEY, AUTO_INCREMENT)
- product_name (VARCHAR(100), NOT NULL)
- color (VARCHAR(30))
- size (VARCHAR(10))

#### 4. Order_Header
- order_id (INT, PRIMARY KEY, AUTO_INCREMENT)
- order_date (DATE, NOT NULL)
- customer_id (INT, NOT NULL, FOREIGN KEY)
- shipping_contact_mech_id (INT, NOT NULL, FOREIGN KEY)
- billing_contact_mech_id (INT, NOT NULL, FOREIGN KEY)

#### 5. Order_Item
- order_item_seq_id (INT, PRIMARY KEY, AUTO_INCREMENT)
- order_id (INT, NOT NULL, FOREIGN KEY)
- product_id (INT, NOT NULL, FOREIGN KEY)
- quantity (INT, NOT NULL)
- status (VARCHAR(20), NOT NULL)

## API Endpoints

### 1. Create an Order
POST /orders

Request Body:
```json
{
  "orderDate": "2025-12-27",
  "customerId": 1,
  "shippingContactMechId": 1,
  "billingContactMechId": 2,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "status": "ORDERED"
    }
  ]
}
```

### 2. Retrieve Order Details
GET /orders/{order_id}

### 3. Update an Order
PUT /orders/{order_id}

Request Body:
```json
{
  "shippingContactMechId": 1,
  "billingContactMechId": 2
}
```

### 4. Delete an Order
DELETE /orders/{order_id}

### 5. Add an Order Item
POST /orders/{order_id}/items

Request Body:
```json
{
  "productId": 3,
  "quantity": 1,
  "status": "ORDERED"
}
```

### 6. Update an Order Item
PUT /orders/{order_id}/items/{order_item_seq_id}

Request Body:
```json
{
  "quantity": 2,
  "status": "SHIPPED"
}
```

### 7. Delete an Order Item
DELETE /orders/{order_id}/items/{order_item_seq_id}

## Project Structure
```
src/main/java/com/hotwax/
├── model/
│   ├── Customer.java
│   ├── ContactMech.java
│   ├── Product.java
│   ├── OrderHeader.java
│   └── OrderItem.java
├── repository/
│   ├── CustomerRepository.java
│   ├── ContactMechRepository.java
│   ├── ProductRepository.java
│   ├── OrderHeaderRepository.java
│   └── OrderItemRepository.java
├── dto/
│   ├── OrderRequestDTO.java
│   ├── OrderResponseDTO.java
│   ├── OrderUpdateDTO.java
│   ├── OrderItemDTO.java
│   ├── OrderItemResponseDTO.java
│   └── OrderItemUpdateDTO.java
├── service/
│   └── OrderService.java
├── controller/
│   └── OrderController.java
└── exception/
    └── GlobalExceptionHandler.java
```

## Technologies Used
- Java 21
- Spring Boot 3.5.0
- Spring Data JPA
- MySQL 9.5.0
- Lombok
- Maven 3.9.12

## Setup Instructions

### Prerequisites
- JDK 21
- MySQL Server 9.5.0
- Maven 3.9.12

### Database Setup
```sql
CREATE DATABASE hotwax_assignment;
```

Run the SQL scripts to create tables and insert sample data.

### Application Configuration
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotwax_assignment
spring.datasource.username=root
spring.datasource.password=
```

### Build and Run
```bash
./mvnw clean package
java -jar target/assignment-0.0.1-SNAPSHOT.jar
```

Server runs on: http://localhost:8080

## Testing Scenarios

### Scenario 1: Create an Order
```bash
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{
  "orderDate": "2025-12-27",
  "customerId": 1,
  "shippingContactMechId": 1,
  "billingContactMechId": 2,
  "orderItems": [
    {"productId": 1, "quantity": 2, "status": "ORDERED"},
    {"productId": 2, "quantity": 1, "status": "ORDERED"}
  ]
}'
```

### Scenario 2: Retrieve Order Details
```bash
curl http://localhost:8080/orders/1
```

### Scenario 3: Update Order Item
```bash
curl -X PUT http://localhost:8080/orders/1/items/2 \
  -H "Content-Type: application/json" \
  -d '{"quantity": 2}'
```

### Scenario 4: Add Order Item
```bash
curl -X POST http://localhost:8080/orders/1/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 3, "quantity": 1, "status": "ORDERED"}'
```

### Scenario 5: Delete Order Item
```bash
curl -X DELETE http://localhost:8080/orders/1/items/1
```

### Scenario 6: Delete Order
```bash
curl -X DELETE http://localhost:8080/orders/1
```

## Error Handling
The API returns appropriate HTTP status codes:
- 200 OK: Successful GET/PUT requests
- 201 Created: Successful POST requests
- 204 No Content: Successful DELETE requests
- 400 Bad Request: Validation errors
- 404 Not Found: Resource not found
- 500 Internal Server Error: Unexpected errors

## Sample Data
The database includes:
- 2 Customers (John Doe, Jane Smith)
- 3 Contact Mechanisms
- 5 Products (T-Shirt, Jeans, Sneakers, Jacket, Hat)

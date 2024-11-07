<p align="center">
    <img src="./docs/logo.png" align="center" width="30%" />
</p>
<p align="center"><h1 align="center">Payment Processing API</h1></p>
<p align="center">Built with the tools and technologies:</p>
<p align="center">
	<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="java">
	<img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white" alt="Gradle">
	<img src="https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge&logo=Docker&logoColor=white" alt="Docker">
</p>
<br>

<details><summary>Table of Contents</summary>

- [📍 Overview](#-overview)
- [🚀 Getting Started](#-getting-started)
  - [☑️ Prerequisites](#-prerequisites)
  - [⚙️ Installation](#-installation)
  - [🤖 Usage](#🤖-usage)
  - [🧪 Testing](#🧪-testing)
- [👾 Features](#-features)

</details>
<hr>

## 📍 Overview

This project is a **Payment Processing API** built with **Spring Boot**. It handles payment requests by validating input data, processing payments through an external acquiring bank API, and providing appropriate responses. The API supports creating new payments and retrieving existing payment details.

## 🚀 Getting Started

### ☑️ Prerequisites

- **Java 17** or higher
- **Gradle** build tool
- **External Acquiring Bank API** running at `http://localhost:8080/payments`

### ⚙️ Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/payment-processing-api.git
   cd payment-processing-api
   ```

2. **Build the Project**

   ```bash
   ./gradlew clean build
   ```

### 🤖 Usage

1. **Start the External Acquiring Bank API**

   Ensure the external API is running at `http://localhost:8080/payments`. This API should accept payment requests and return authorization responses.

2. **Configure Application Properties**

   Update `src/main/resources/application.properties` if necessary:

   ```properties
   # Application port
   server.port=8090

   # External Acquiring Bank API Base URL
   acquiring.bank.base-url=http://localhost:8080
   ```

3. **Run the Application**

   ```bash
   ./gradlew bootRun
   ```

   The API will be accessible at `http://localhost:8090`.

### 🧪 Testing

**Run Unit Tests**

```bash
./gradlew test
```

**Test Coverage**

- **Controller Tests**: Uses MockMvc to test REST endpoints.
- **Service Tests**: Tests business logic and interaction with the in-memory database.
- **Client Tests**: Mocks external API calls to test the AcquiringBankClient.

**Example Queries**

1. Process Authorised Payment

   ```bash
   curl -X POST http://localhost:8090/api/v1/payments \
       -H "Content-Type: application/json" \
       -d '{
             "cardNumber": "2222405343248877",
             "expiryMonth": 4,
             "expiryYear": 2025,
             "currency": "GBP",
             "amount": "100",
             "cvv": "123"
           }'
   ```

   - response

   ```json
   {
     "id": "959dcb52-804d-4065-a255-93d1b9a66a90",
     "status": "Authorized",
     "cardNumberLastFour": 8877,
     "expiryMonth": 4,
     "expiryYear": 2025,
     "currency": "GBP",
     "amount": 100
   }
   ```

1. Process Declined Payment

   ```
   curl -X POST http://localhost:8090/api/v1/payments \
       -H "Content-Type: application/json" \
       -d '{
             "cardNumber": "2222405343248112",
             "expiryMonth": 1,
             "expiryYear": 2026,
             "currency": "USD",
             "amount": "60000",
             "cvv": "456"
           }'
   ```

   - response

   ```json
   {
     "id": "e56b54f0-7c30-433e-95d8-ac484b58ab96",
     "status": "Declined",
     "cardNumberLastFour": 8112,
     "expiryMonth": 1,
     "expiryYear": 2026,
     "currency": "USD",
     "amount": 60000
   }
   ```

1. Fetch payment details

   ```sh
   curl -X GET http://localhost:8090/api/v1/payments/e56b54f0-7c30-433e-95d8-ac484b58ab96
   ```

   - response

   ```json
   {
     "id": "e56b54f0-7c30-433e-95d8-ac484b58ab96",
     "status": "Declined",
     "cardNumberLastFour": 8112,
     "expiryMonth": 1,
     "expiryYear": 2026,
     "currency": "USD",
     "amount": 60000
   }
   ```

## 👾 Features

- **Input Validation**: Uses Java Bean Validation (JSR-380) to enforce validation rules on incoming payment requests.
- **External API Integration**: Communicates with an external acquiring bank API to process payments.
- **Exception Handling**: Provides meaningful error responses using a global exception handler.
- **Security**: Sensitive information like full card numbers and CVV codes are not stored or exposed.
- **Testing**: Includes unit tests for controllers, services, and clients using Mockito and MockMvc.
- **Modular Design**: Separates concerns across controllers, services, clients, and models for maintainability.

### Technologies Used

- **Java 17**
- **Spring Boot 3**
- **Spring WebFlux** (for `WebClient`)
- **Spring Validation**
- **Lombok** (for boilerplate code reduction)
- **MapStruct** (for object mapping)
- **Mockito** (for mocking in tests)
- **JUnit 5** (for unit testing)
- **Gradle** (as the build tool)
- **Hibernate Validator** (reference implementation for Bean Validation)
- **SLF4J** (for logging)

### API Endpoints

#### **Process Payment**

- **URL**: `/api/v1/payments`
- **Method**: `POST`
- **Description**: Processes a payment request by validating input data and communicating with the external acquiring bank API.

##### **Request Body**

```json
{
  "cardNumber": "1234567890123456",
  "cvv": "123",
  "expiryMonth": 12,
  "expiryYear": 2025,
  "currency": "USD",
  "amount": 1050
}
```

#### **Validation Rules**

| Field            | Validation Rules                                     |
| ---------------- | ---------------------------------------------------- |
| **Card Number**  | Required; numeric; 14-19 digits                      |
| **CVV**          | Required; numeric; 3-4 digits                        |
| **Expiry Month** | Required; integer between 1 and 12                   |
| **Expiry Year**  | Required; integer; must be in the future             |
| **Expiry Date**  | Combination of month and year must be in the future  |
| **Currency**     | Required; 3 characters; one of `USD`, `EUR`, `GBP`   |
| **Amount**       | Required; positive integer (in minor currency units) |

##### **Response**

- **Success (200 OK)**

  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "AUTHORIZED",
    "cardNumberLastFour": 3456,
    "expiryMonth": 12,
    "expiryYear": 2025,
    "currency": "USD",
    "amount": 1050
  }
  ```

- **Validation Error (400 Bad Request)**

  ```json
  {
    "status": "BAD_REQUEST",
    "message": ["ErrorMessage"]
  }
  ```

#### **Get Payment Details**

- **URL**: `/api/v1/payments/{id}`
- **Method**: `GET`
- **Description**: Retrieves payment details by payment ID.

##### **Response**

- **Success (200 OK)**

  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "AUTHORIZED",
    "cardNumberLastFour": 3456,
    "expiryMonth": 12,
    "expiryYear": 2025,
    "currency": "USD",
    "amount": 1050
  }
  ```

- **Not Found (404 Not Found)**

  ```json
  {
    "status": "NOT_FOUND",
    "messages": ["Payment not found with id: {id}"]
  }
  ```

### Design Considerations

- **Separation of Concerns**

  - **Controller Layer**: Handles HTTP requests and responses.
  - **Service Layer**: Contains business logic and communicates with clients.
  - **Client Layer**: Manages external API calls (e.g., AcquiringBankClient).
  - **Model and DTOs**: Defines data structures for internal use and data transfer.

- **Validation**

  - Leveraged **Java Bean Validation (JSR-380)** using **Hibernate Validator**.
  - Input validation is performed at the DTO level using annotations like `@NotNull`, `@Pattern`, `@Size`, and custom `@AssertTrue`.

- **Exception Handling**

  - Implemented a **GlobalExceptionHandler** using `@ControllerAdvice` to handle exceptions globally.
  - Provides consistent error responses with meaningful messages and HTTP status codes.

- **External API Integration**

  - The **AcquiringBankClient** abstracts the communication with the external acquiring bank API.
  - Uses **WebClient** from Spring WebFlux for non-blocking HTTP calls.
  - Allows for easier testing and mocking of external API interactions.

- **Security**

  - **Sensitive Data Handling**: Full card numbers and CVV codes are not stored or exposed in any responses.
  - **Data Exposure**: Only the last four digits of the card number are exposed in responses.
  - **Logging**: Avoids logging sensitive information.

- **Testing**

  - Used **Mockito** for mocking dependencies and **JUnit 5** for writing unit tests.
  - **MockMvc** is used to test controller endpoints without starting the server.
  - **ExchangeFunction** is mocked to simulate external API responses in client tests.

- **Scalability and Maintainability**

  - Modular architecture allows for easy scaling and maintenance.
  - Future enhancements (e.g., adding more currencies or payment methods) can be accommodated with minimal changes.

### Assumptions

- **External Acquiring Bank API**

  - Assumes the external API is available at `http://localhost:8080/payments`.
  - The expected request and response structures match the provided examples.

- **Supported Currencies**

  - Only supports three currencies: `USD`, `EUR`, and `GBP`.

- **Amount Representation**

  - Amounts are provided in minor currency units (e.g., cents for USD).

- **Data Persistence**

  - Uses an in-memory data structure (`HashMap`) for storing payment data.
  - Not suitable for production environments; a persistent database should be used for real applications.

- **Expiry Date Validation**

  - The combination of `expiryMonth` and `expiryYear` must represent a future date.
  - Does not account for time zones or exact expiration times.

- **Testing Environment**

  - Tests are performed under the assumption that the external API and application are running locally.
  - Mocked external API responses are used to simulate different scenarios.

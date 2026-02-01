# RFC 7807 Error Handling for Spring Boot

A proof-of-concept implementation of standardized error handling using [RFC 7807 Problem Details](https://tools.ietf.org/html/rfc7807) in Spring Boot.

## Features

- **RFC 7807 Compliant** - Uses Spring's `ProblemDetail` with proper `type` URI for programmatic error identification
- **Extended Error Details** - Custom `errors` array with code, message, and field information
- **Field-Level Validation** - Validation errors include the specific field that failed
- **Externalized Messages** - Error messages stored in `messages.properties` for i18n support
- **Structured Error Codes** - Categorized error codes (e.g., `BRE-C1-0001`) for easy identification

## Error Response Format

```json
{
  "type": "https://api.example.com/errors/BRE-C1-0002",
  "title": "Bad Request",
  "status": 400,
  "detail": "Discount greater than 30% not allowed.",
  "instance": "/sales/calculate",
  "errors": [
    {
      "code": "BRE-C1-0002",
      "message": "Discount greater than 30% not allowed.",
      "field": null
    }
  ]
}
```

For validation errors, the `field` property identifies the problematic field:

```json
{
  "type": "https://api.example.com/errors/validation-error",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/sales/calculate",
  "errors": [
    {
      "code": "BRE-C2-0001",
      "message": "Base price should be greater than zero.",
      "field": "basePrice"
    }
  ]
}
```

## Project Structure

```
src/main/java/com/bioudiamine/errors_rfc/
├── controller/
│   └── SalesController.java        # Sample REST controller
├── exception/
│   ├── ErrorCode.java              # Error code enum
│   ├── ErrorMessage.java           # Error message record
│   ├── ExtendedProblemDetail.java  # RFC 7807 extension
│   ├── GlobalExceptionHandler.java # Centralized exception handling
│   ├── InvalidInputException.java  # Custom business exception
│   └── MessageService.java         # i18n message resolution
└── request/
    ├── OperationRequest.java       # Request DTO with validation
    └── OperationResult.java        # Response DTO
```

## Error Code Convention

Error codes follow the pattern: `BRE-{Category}-{Number}`

| Category | Description |
|----------|-------------|
| C1 | Discount rules |
| C2 | Price rules |

## API Endpoints

### POST /sales/calculate

Calculate the selling price with optional discount.

**Request:**
```json
{
  "basePrice": 100.0,
  "discount": 20.0
}
```

**Response:**
```json
{
  "basePrice": 100.0,
  "discount": 20.0,
  "sellingPrice": 80.0
}
```

## Requirements

- Java 21
- Spring Boot 4.0.2

## Running the Application

```bash
./mvnw spring-boot:run
```

## Testing Error Scenarios

```bash
# Valid request
curl -X POST http://localhost:8080/sales/calculate \
  -H "Content-Type: application/json" \
  -d '{"basePrice": 100, "discount": 20}'

# Discount exceeds limit (> 30%)
curl -X POST http://localhost:8080/sales/calculate \
  -H "Content-Type: application/json" \
  -d '{"basePrice": 100, "discount": 50}'

# Missing required field
curl -X POST http://localhost:8080/sales/calculate \
  -H "Content-Type: application/json" \
  -d '{"discount": 10}'

# Multiple validation errors
curl -X POST http://localhost:8080/sales/calculate \
  -H "Content-Type: application/json" \
  -d '{"basePrice": -50, "discount": -10}'
```

## License

MIT

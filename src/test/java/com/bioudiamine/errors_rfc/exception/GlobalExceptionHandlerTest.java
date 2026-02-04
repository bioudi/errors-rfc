package com.bioudiamine.errors_rfc.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(messageService);
        when(webRequest.getDescription(false)).thenReturn("uri=/sales/calculate");
    }

    @Nested
    @DisplayName("handleMethodArgumentNotValid")
    class HandleMethodArgumentNotValid {

        @Test
        @DisplayName("Should return ProblemDetail with single validation error")
        void shouldReturnProblemDetailWithSingleValidationError() {
            FieldError fieldError = new FieldError("operationRequest", "basePrice", "BRE-C2-0001");
            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
            when(messageService.getMessage("BRE-C2-0001")).thenReturn("Base price must be positive");

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(ExtendedProblemDetail.class);

            ExtendedProblemDetail problemDetail = (ExtendedProblemDetail) response.getBody();
            assertThat(problemDetail.getStatus()).isEqualTo(400);
            assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
            assertThat(problemDetail.getDetail()).isEqualTo("Validation failed");
            assertThat(problemDetail.getInstance().toString()).isEqualTo("/sales/calculate");
            assertThat(problemDetail.getErrors()).hasSize(1);
            assertThat(problemDetail.getErrors().get(0).code()).isEqualTo("BRE-C2-0001");
            assertThat(problemDetail.getErrors().get(0).message()).isEqualTo("Base price must be positive");
            assertThat(problemDetail.getErrors().get(0).field()).isEqualTo("basePrice");
        }

        @Test
        @DisplayName("Should return ProblemDetail with multiple validation errors")
        void shouldReturnProblemDetailWithMultipleValidationErrors() {
            FieldError basePriceError = new FieldError("operationRequest", "basePrice", "BRE-C2-0001");
            FieldError discountError = new FieldError("operationRequest", "discount", "BRE-C2-0002");
            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(basePriceError, discountError));
            when(messageService.getMessage("BRE-C2-0001")).thenReturn("Base price must be positive");
            when(messageService.getMessage("BRE-C2-0002")).thenReturn("Discount must be positive");

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            ExtendedProblemDetail problemDetail = (ExtendedProblemDetail) response.getBody();
            assertThat(problemDetail.getErrors()).hasSize(2);
            assertThat(problemDetail.getErrors())
                .extracting(ErrorMessage::code)
                .containsExactlyInAnyOrder("BRE-C2-0001", "BRE-C2-0002");
            assertThat(problemDetail.getErrors())
                .extracting(ErrorMessage::field)
                .containsExactlyInAnyOrder("basePrice", "discount");
        }

        @Test
        @DisplayName("Should set correct type URI for validation errors")
        void shouldSetCorrectTypeUriForValidationErrors() {
            FieldError fieldError = new FieldError("operationRequest", "basePrice", "BRE-C2-0001");
            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
            when(messageService.getMessage("BRE-C2-0001")).thenReturn("Base price must be positive");

            ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

            ExtendedProblemDetail problemDetail = (ExtendedProblemDetail) response.getBody();
            assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/validation-error");
        }
    }

    @Nested
    @DisplayName("handleInvalidInputException")
    class HandleInvalidInputException {

        @Test
        @DisplayName("Should return ProblemDetail for FREE_SALE_NOT_ALLOWED error")
        void shouldReturnProblemDetailForFreeSaleNotAllowed() {
            InvalidInputException exception = new InvalidInputException(ErrorCode.FREE_SALE_NOT_ALLOWED);
            when(messageService.getMessage("BRE-C1-0001")).thenReturn("Free sale is not allowed.");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleInvalidInputException(exception, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getStatus()).isEqualTo(400);
            assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
            assertThat(problemDetail.getDetail()).isEqualTo("Free sale is not allowed.");
            assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C1-0001");
            assertThat(problemDetail.getInstance().toString()).isEqualTo("/sales/calculate");
            assertThat(problemDetail.getErrors()).hasSize(1);
            assertThat(problemDetail.getErrors().get(0).code()).isEqualTo("BRE-C1-0001");
            assertThat(problemDetail.getErrors().get(0).message()).isEqualTo("Free sale is not allowed.");
        }

        @Test
        @DisplayName("Should return ProblemDetail for DISCOUNT_EXCEEDS_LIMIT error")
        void shouldReturnProblemDetailForDiscountExceedsLimit() {
            InvalidInputException exception = new InvalidInputException(ErrorCode.DISCOUNT_EXCEEDS_LIMIT);
            when(messageService.getMessage("BRE-C1-0002")).thenReturn("Discount greater than 30% not allowed.");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleInvalidInputException(exception, webRequest);

            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getDetail()).isEqualTo("Discount greater than 30% not allowed.");
            assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C1-0002");
            assertThat(problemDetail.getErrors().get(0).code()).isEqualTo("BRE-C1-0002");
        }

        @Test
        @DisplayName("Should return ProblemDetail for INVALID_BASE_PRICE error")
        void shouldReturnProblemDetailForInvalidBasePrice() {
            InvalidInputException exception = new InvalidInputException(ErrorCode.INVALID_BASE_PRICE);
            when(messageService.getMessage("BRE-C2-0001")).thenReturn("Base price must be positive");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleInvalidInputException(exception, webRequest);

            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C2-0001");
            assertThat(problemDetail.getErrors().get(0).code()).isEqualTo("BRE-C2-0001");
        }

        @Test
        @DisplayName("Should return ProblemDetail for INVALID_DISCOUNT error")
        void shouldReturnProblemDetailForInvalidDiscount() {
            InvalidInputException exception = new InvalidInputException(ErrorCode.INVALID_DISCOUNT);
            when(messageService.getMessage("BRE-C2-0002")).thenReturn("Discount must be positive");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleInvalidInputException(exception, webRequest);

            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C2-0002");
            assertThat(problemDetail.getErrors().get(0).code()).isEqualTo("BRE-C2-0002");
        }
    }

    @Nested
    @DisplayName("handleAllExceptions")
    class HandleAllExceptions {

        @Test
        @DisplayName("Should return 500 Internal Server Error for generic exception")
        void shouldReturnInternalServerErrorForGenericException() {
            Exception exception = new Exception("Unexpected error occurred");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleAllExceptions(exception, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getStatus()).isEqualTo(500);
            assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
            assertThat(problemDetail.getDetail()).isEqualTo("Unexpected error occurred");
            assertThat(problemDetail.getInstance().toString()).isEqualTo("/sales/calculate");
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error for NullPointerException")
        void shouldReturnInternalServerErrorForNullPointerException() {
            NullPointerException exception = new NullPointerException("Null value encountered");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleAllExceptions(exception, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getDetail()).isEqualTo("Null value encountered");
        }

        @Test
        @DisplayName("Should set correct type URI for internal server error")
        void shouldSetCorrectTypeUriForInternalServerError() {
            Exception exception = new Exception("Some error");

            ResponseEntity<ExtendedProblemDetail> response = handler.handleAllExceptions(exception, webRequest);

            ExtendedProblemDetail problemDetail = response.getBody();
            assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/internal_server_error");
        }
    }
}

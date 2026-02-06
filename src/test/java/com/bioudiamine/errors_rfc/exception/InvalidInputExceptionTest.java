package com.bioudiamine.errors_rfc.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidInputExceptionTest {

    @Test
    void constructor_setsErrorCodeAndMessage() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.FREE_SALE_NOT_ALLOWED);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FREE_SALE_NOT_ALLOWED);
        assertThat(exception.getMessage()).isEqualTo("BRE-C1-0001");
    }

    @Test
    void getCode_returnsErrorCodeString() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.DISCOUNT_EXCEEDS_LIMIT);

        assertThat(exception.getCode()).isEqualTo("BRE-C1-0002");
    }

    @Test
    void getStatusCode_returnsBadRequest() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.FREE_SALE_NOT_ALLOWED);

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getBody_returnsProblemDetailWithCorrectType() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.DISCOUNT_EXCEEDS_LIMIT);

        ProblemDetail body = exception.getBody();

        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getDetail()).isEqualTo("BRE-C1-0002");
        assertThat(body.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C1-0002");
    }

    @Test
    void getBody_withInvalidBasePrice_setsCorrectTypeUri() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.INVALID_BASE_PRICE);

        ProblemDetail body = exception.getBody();

        assertThat(body.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C2-0001");
        assertThat(body.getDetail()).isEqualTo("BRE-C2-0001");
    }

    @Test
    void getBody_withInvalidDiscount_setsCorrectTypeUri() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.INVALID_DISCOUNT);

        ProblemDetail body = exception.getBody();

        assertThat(body.getType().toString()).isEqualTo("https://api.example.com/errors/BRE-C2-0002");
        assertThat(body.getDetail()).isEqualTo("BRE-C2-0002");
    }

    @Test
    void isRuntimeException() {
        InvalidInputException exception = new InvalidInputException(ErrorCode.FREE_SALE_NOT_ALLOWED);

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}

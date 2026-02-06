package com.bioudiamine.errors_rfc.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExtendedProblemDetailTest {

    @Test
    void forStatusAndDetail_withErrorCode_setsAllFieldsCorrectly() {
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Something went wrong", "custom-error-code");

        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Something went wrong");
        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/custom-error-code");
    }

    @Test
    void forStatusAndDetail_withoutErrorCode_usesStatusNameAsErrorCode() {
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, "Resource not found");

        assertThat(problemDetail.getStatus()).isEqualTo(404);
        assertThat(problemDetail.getDetail()).isEqualTo("Resource not found");
        assertThat(problemDetail.getTitle()).isEqualTo("Not Found");
        assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/not_found");
    }

    @Test
    void forStatusAndDetail_withInternalServerError_setsCorrectType() {
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

        assertThat(problemDetail.getStatus()).isEqualTo(500);
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problemDetail.getType().toString()).isEqualTo("https://api.example.com/errors/internal_server_error");
    }

    @Test
    void addError_withCodeAndMessage_addsErrorToList() {
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed");

        problemDetail.addError("ERR-001", "Field is required");

        @SuppressWarnings("unchecked")
        List<ErrorMessage> errors = (List<ErrorMessage>) problemDetail.getProperties().get("errors");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).code()).isEqualTo("ERR-001");
        assertThat(errors.get(0).message()).isEqualTo("Field is required");
        assertThat(errors.get(0).field()).isNull();
    }

    @Test
    void addError_withCodeMessageAndField_addsErrorWithField() {
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed");

        problemDetail.addError("ERR-002", "Must be positive", "amount");

        @SuppressWarnings("unchecked")
        List<ErrorMessage> errors = (List<ErrorMessage>) problemDetail.getProperties().get("errors");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).code()).isEqualTo("ERR-002");
        assertThat(errors.get(0).message()).isEqualTo("Must be positive");
        assertThat(errors.get(0).field()).isEqualTo("amount");
    }

    @Test
    void addError_multipleErrors_accumulatesInList() {
        ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed");

        problemDetail.addError("ERR-001", "Field is required", "name");
        problemDetail.addError("ERR-002", "Must be positive", "price");
        problemDetail.addError("ERR-003", "Invalid format");

        @SuppressWarnings("unchecked")
        List<ErrorMessage> errors = (List<ErrorMessage>) problemDetail.getProperties().get("errors");
        assertThat(errors).hasSize(3);
        assertThat(errors.get(0).field()).isEqualTo("name");
        assertThat(errors.get(1).field()).isEqualTo("price");
        assertThat(errors.get(2).field()).isNull();
    }

    @Test
    void defaultConstructor_createsEmptyProblemDetail() {
        ExtendedProblemDetail problemDetail = new ExtendedProblemDetail();

        assertThat(problemDetail.getStatus()).isEqualTo(500);
        assertThat(problemDetail.getProperties()).isNull();
    }
}

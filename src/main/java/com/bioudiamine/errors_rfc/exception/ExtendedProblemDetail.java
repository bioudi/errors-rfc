package com.bioudiamine.errors_rfc.exception;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "RFC 7807 Problem Details with extended error information")
public class ExtendedProblemDetail extends ProblemDetail {

  private static final String TYPE_BASE_URI = "https://api.example.com/errors/";

  private final List<ErrorMessage> errors = new ArrayList<>();

  public ExtendedProblemDetail() {
    super();
  }

  @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))
  @Schema(description = "List of detailed error messages")
  public List<ErrorMessage> getErrors() {
    return errors;
  }

  public static ExtendedProblemDetail forStatusAndDetail(HttpStatus status, String detail, String errorCode) {
    ExtendedProblemDetail problemDetail = new ExtendedProblemDetail();
    problemDetail.setStatus(status.value());
    problemDetail.setDetail(detail);
    problemDetail.setTitle(status.getReasonPhrase());
    problemDetail.setType(URI.create(TYPE_BASE_URI + errorCode));
    return problemDetail;
  }

  public static ExtendedProblemDetail forStatusAndDetail(HttpStatus status, String detail) {
    return forStatusAndDetail(status, detail, status.name().toLowerCase());
  }

  public void addError(String code, String message) {
    errors.add(new ErrorMessage(code, message));
    syncErrorsProperty();
  }

  public void addError(String code, String message, String field) {
    errors.add(new ErrorMessage(code, message, field));
    syncErrorsProperty();
  }

  private void syncErrorsProperty() {
    setProperty("errors", errors);
  }
}

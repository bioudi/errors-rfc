package com.bioudiamine.errors_rfc.exception;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageService messageService;

  public GlobalExceptionHandler(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Validation failed");
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

    ex.getBindingResult().getFieldErrors().forEach(error -> {
        String code = error.getDefaultMessage();
        String message = messageService.getMessage(code);
        problemDetail.addMessage(code, message);
    });

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      @Nullable Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {

    ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
        HttpStatus.valueOf(statusCode.value()), ex.getMessage());
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.addMessage(ex.getClass().getSimpleName().toUpperCase(), ex.getMessage());

    return ResponseEntity.status(statusCode).body(problemDetail);
  }

  @ExceptionHandler(InvalidInputException.class)
  public ExtendedProblemDetail handleInvalidInputException(InvalidInputException e, WebRequest request) {
    String code = e.getCode();
    String message = messageService.getMessage(code);
    ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.addMessage(code, message);
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExtendedProblemDetail> handleAllExceptions(Exception e, WebRequest request) {
    ExtendedProblemDetail problemDetail = ExtendedProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.addMessage(e.getClass().getSimpleName().toUpperCase(), e.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }
}


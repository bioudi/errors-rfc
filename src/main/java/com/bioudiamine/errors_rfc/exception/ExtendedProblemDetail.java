package com.bioudiamine.errors_rfc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.List;

public class ExtendedProblemDetail extends ProblemDetail {

  private List<ErrorMessage> messages = new ArrayList<>();

  public ExtendedProblemDetail() {
    super();
  }

  public static ExtendedProblemDetail forStatusAndDetail(HttpStatus status, String detail) {
    ExtendedProblemDetail problemDetail = new ExtendedProblemDetail();
    problemDetail.setStatus(status.value());
    problemDetail.setDetail(detail);
    problemDetail.setTitle(status.getReasonPhrase());
    return problemDetail;
  }

  public List<ErrorMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ErrorMessage> messages) {
    this.messages = messages;
  }

  public void addMessage(String code, String message) {
    this.messages.add(new ErrorMessage(code, message));
  }
}

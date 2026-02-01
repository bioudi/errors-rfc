package com.bioudiamine.errors_rfc.controller;

import com.bioudiamine.errors_rfc.exception.ErrorCode;
import com.bioudiamine.errors_rfc.exception.InvalidInputException;
import com.bioudiamine.errors_rfc.request.OperationRequest;
import com.bioudiamine.errors_rfc.request.OperationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sales")
public class SalesController {

  @PostMapping("/calculate")
  public ResponseEntity<OperationResult> calculate(
      @Validated @RequestBody OperationRequest operationRequest
  ) {
    Double discount = operationRequest.discount();

    OperationResult operationResult;
    if (discount == null) {
      operationResult = new OperationResult(operationRequest.basePrice(), null, operationRequest.basePrice());
    } else {
      if (discount >= 100) {
        throw new InvalidInputException(ErrorCode.FREE_SALE_NOT_ALLOWED);
      } else if (discount > 30) {
        throw new InvalidInputException(ErrorCode.DISCOUNT_EXCEEDS_LIMIT);
      } else {
        double sellingPrice = operationRequest.basePrice() * (100 - discount) / 100;
        operationResult = new OperationResult(operationRequest.basePrice(), discount, sellingPrice);
      }
    }

    return ResponseEntity.ok(operationResult);
  }
}


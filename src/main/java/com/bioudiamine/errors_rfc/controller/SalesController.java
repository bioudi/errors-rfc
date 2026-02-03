package com.bioudiamine.errors_rfc.controller;

import com.bioudiamine.errors_rfc.exception.ErrorCode;
import com.bioudiamine.errors_rfc.exception.InvalidInputException;
import com.bioudiamine.errors_rfc.request.OperationRequest;
import com.bioudiamine.errors_rfc.request.OperationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sales")
@Tag(name = "Sales", description = "Sales calculation operations")
public class SalesController {

//  @Operation(summary = "Calculate selling price", description = "Calculates the selling price based on base price and optional discount")
//  @ApiResponse(responseCode = "200", description = "Successfully calculated selling price")
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


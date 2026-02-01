package com.bioudiamine.errors_rfc.request;

public record OperationResult(
    Double basePrice,
    Double discount,
    Double sellingPrice
) {}


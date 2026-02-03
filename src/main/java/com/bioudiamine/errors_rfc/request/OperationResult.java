package com.bioudiamine.errors_rfc.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of the selling price calculation")
public record OperationResult(
    @Schema(description = "Original base price", example = "100.0")
    Double basePrice,

    @Schema(description = "Applied discount percentage", example = "20.0")
    Double discount,

    @Schema(description = "Final selling price after discount", example = "80.0")
    Double sellingPrice
) {}


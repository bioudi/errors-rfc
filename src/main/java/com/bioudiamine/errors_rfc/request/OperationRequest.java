package com.bioudiamine.errors_rfc.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.lang.Nullable;

@Schema(description = "Request for calculating selling price")
public record OperationRequest(
    @Schema(description = "Base price of the product", example = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "BRE-C2-0001")
    @Positive(message = "BRE-C2-0001")
    Double basePrice,

    @Schema(description = "Discount percentage (0-30%)", example = "20.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Positive(message = "BRE-C2-0002")
    Double discount
) {}


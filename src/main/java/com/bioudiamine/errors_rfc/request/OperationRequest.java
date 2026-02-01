package com.bioudiamine.errors_rfc.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.lang.Nullable;

public record OperationRequest(
    @NotNull(message = "BRE-C2-0001")
    @Positive(message = "BRE-C2-0001")
    Double basePrice,

    @Nullable
    @Positive(message = "BRE-C2-0002")
    Double discount
) {}


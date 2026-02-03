package com.bioudiamine.errors_rfc.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Individual error detail")
public record ErrorMessage(
    @Schema(description = "Error code", example = "BRE-C1-0002")
    String code,

    @Schema(description = "Error message", example = "Discount greater than 30% not allowed.")
    String message,

    @Schema(description = "Field that caused the error (if applicable)", example = "discount")
    String field
) implements Serializable {
    private static final long serialVersionUID = -139762416199957L;

    public ErrorMessage(String code, String message) {
        this(code, message, null);
    }
}

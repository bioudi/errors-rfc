package com.bioudiamine.errors_rfc.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@Schema(description = "RFC 7807 Problem Details with extended error information")
public record ErrorResponseSchema(
    @Schema(description = "URI reference identifying the problem type", example = "https://api.example.com/errors/BRE-C1-0002")
    URI type,

    @Schema(description = "Short human-readable summary", example = "Bad Request")
    String title,

    @Schema(description = "HTTP status code", example = "400")
    int status,

    @Schema(description = "Human-readable explanation", example = "Discount greater than 30% not allowed.")
    String detail,

    @Schema(description = "URI reference identifying the specific occurrence", example = "/sales/calculate")
    URI instance,

    @Schema(description = "List of detailed error messages")
    List<ErrorMessage> errors
) {}

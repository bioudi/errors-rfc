package com.bioudiamine.errors_rfc.config;

import com.bioudiamine.errors_rfc.exception.ErrorResponseSchema;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    var schemas = ModelConverters.getInstance()
        .resolveAsResolvedSchema(new AnnotatedType(ErrorResponseSchema.class));

    return new OpenAPI()
        .info(new Info()
            .title("Sales API")
            .version("1.0")
            .description("REST API demonstrating RFC 7807 Problem Details for error handling"))
        .components(new Components().schemas(schemas.referencedSchemas));
  }

  @Bean
  public OperationCustomizer globalErrorResponseCustomizer() {
    return (operation, handlerMethod) -> {
      Schema<?> errorSchema = new Schema<>().$ref("#/components/schemas/ErrorResponseSchema");
      Content errorContent = new Content().addMediaType("application/problem+json",
          new MediaType().schema(errorSchema));

      operation.getResponses()
          .addApiResponse("400", new ApiResponse()
              .description("Bad Request - Validation failed or business rule violation")
              .content(errorContent))
          .addApiResponse("401", new ApiResponse()
              .description("Unauthorized - Authentication required")
              .content(errorContent))
          .addApiResponse("403", new ApiResponse()
              .description("Forbidden - Insufficient permissions")
              .content(errorContent));

      return operation;
    };
  }
}

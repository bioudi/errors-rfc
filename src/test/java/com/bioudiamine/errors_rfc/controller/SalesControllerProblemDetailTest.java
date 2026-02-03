package com.bioudiamine.errors_rfc.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Sales Controller - ProblemDetail Response Tests")
class SalesControllerProblemDetailTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String CALCULATE_ENDPOINT = "/sales/calculate";
    private static final String PROBLEM_JSON = "application/problem+json";

    @Nested
    @DisplayName("Authentication Errors (401 Unauthorized)")
    class AuthenticationErrors {

        @Test
        @DisplayName("Should return ProblemDetail when no credentials provided")
        void shouldReturnProblemDetailWhenNoCredentials() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0}
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/unauthorized"))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT));
        }

    }

    @Nested
    @DisplayName("Authorization Errors (403 Forbidden)")
    class AuthorizationErrors {

        @Test
        @DisplayName("Should return ProblemDetail when user lacks ADMIN role")
        void shouldReturnProblemDetailWhenUserLacksAdminRole() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("user", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0}
                        """))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/forbidden"))
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT));
        }
    }

    @Nested
    @DisplayName("Validation Errors (400 Bad Request)")
    class ValidationErrors {

        @Test
        @DisplayName("Should return ProblemDetail when basePrice is null")
        void shouldReturnProblemDetailWhenBasePriceIsNull() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"discount": 10.0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].code", hasItem("BRE-C2-0001")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("basePrice")));
        }

        @Test
        @DisplayName("Should return ProblemDetail when basePrice is negative")
        void shouldReturnProblemDetailWhenBasePriceIsNegative() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": -50.0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].code", hasItem("BRE-C2-0001")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("basePrice")));
        }

        @Test
        @DisplayName("Should return ProblemDetail when basePrice is zero")
        void shouldReturnProblemDetailWhenBasePriceIsZero() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].code", hasItem("BRE-C2-0001")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("basePrice")));
        }

        @Test
        @DisplayName("Should return ProblemDetail when discount is negative")
        void shouldReturnProblemDetailWhenDiscountIsNegative() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": -10.0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].code", hasItem("BRE-C2-0002")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("discount")));
        }

        @Test
        @DisplayName("Should return ProblemDetail when discount is zero")
        void shouldReturnProblemDetailWhenDiscountIsZero() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].code", hasItem("BRE-C2-0002")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("discount")));
        }

        @Test
        @DisplayName("Should return ProblemDetail with multiple errors when both fields are invalid")
        void shouldReturnProblemDetailWithMultipleErrors() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": -100.0, "discount": -10.0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].code", hasItems("BRE-C2-0001", "BRE-C2-0002")))
                .andExpect(jsonPath("$.errors[*].field", hasItems("basePrice", "discount")));
        }

        @Test
        @DisplayName("Should return ProblemDetail when request body is empty")
        void shouldReturnProblemDetailWhenRequestBodyIsEmpty() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].code", hasItem("BRE-C2-0001")));
        }
    }

    @Nested
    @DisplayName("Business Rule Errors (400 Bad Request)")
    class BusinessRuleErrors {

        @Test
        @DisplayName("Should return ProblemDetail when discount is 100% (free sale)")
        void shouldReturnProblemDetailWhenDiscountIs100Percent() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 100}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0001"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Free sale is not allowed."))
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0001"))
                .andExpect(jsonPath("$.errors[0].message").value("Free sale is not allowed."));
        }

        @Test
        @DisplayName("Should return ProblemDetail when discount exceeds 100%")
        void shouldReturnProblemDetailWhenDiscountExceeds100Percent() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 150}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0001"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Free sale is not allowed."))
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0001"));
        }

        @Test
        @DisplayName("Should return ProblemDetail when discount exceeds 30% limit")
        void shouldReturnProblemDetailWhenDiscountExceeds30PercentLimit() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 50}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0002"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Discount greater than 30% not allowed."))
                .andExpect(jsonPath("$.instance").value(CALCULATE_ENDPOINT))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0002"))
                .andExpect(jsonPath("$.errors[0].message").value("Discount greater than 30% not allowed."));
        }

        @Test
        @DisplayName("Should return ProblemDetail when discount is just above 30%")
        void shouldReturnProblemDetailWhenDiscountIsJustAbove30Percent() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 30.01}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0002"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0002"));
        }
    }

    @Nested
    @DisplayName("Success Scenarios (200 OK)")
    class SuccessScenarios {

        @Test
        @DisplayName("Should return success response when request is valid without discount")
        void shouldReturnSuccessWhenValidRequestWithoutDiscount() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0}
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.basePrice").value(100.0))
                .andExpect(jsonPath("$.discount").doesNotExist())
                .andExpect(jsonPath("$.sellingPrice").value(100.0));
        }

        @Test
        @DisplayName("Should return success response when request is valid with discount")
        void shouldReturnSuccessWhenValidRequestWithDiscount() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 20}
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.basePrice").value(100.0))
                .andExpect(jsonPath("$.discount").value(20.0))
                .andExpect(jsonPath("$.sellingPrice").value(80.0));
        }

        @Test
        @DisplayName("Should return success response when discount is exactly 30%")
        void shouldReturnSuccessWhenDiscountIsExactly30Percent() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 200.0, "discount": 30}
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.basePrice").value(200.0))
                .andExpect(jsonPath("$.discount").value(30.0))
                .andExpect(jsonPath("$.sellingPrice").value(140.0));
        }
    }

    @Nested
    @DisplayName("ProblemDetail Structure Validation")
    class ProblemDetailStructureValidation {

        @Test
        @DisplayName("Business error ProblemDetail should have all RFC 7807 required fields")
        void businessErrorShouldHaveAllRequiredFields() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0, "discount": 100}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type", startsWith("https://api.example.com/errors/")))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.detail").isString())
                .andExpect(jsonPath("$.instance").isString());
        }

        @Test
        @DisplayName("Validation error ProblemDetail should have errors array with correct structure")
        void validationErrorShouldHaveErrorsArrayWithCorrectStructure() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("admin", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": -100.0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].code").isString())
                .andExpect(jsonPath("$.errors[0].message").isString())
                .andExpect(jsonPath("$.errors[0].field").isString());
        }

        @Test
        @DisplayName("Security error ProblemDetail should follow RFC 7807 structure")
        void securityErrorShouldFollowRfc7807Structure() throws Exception {
            mockMvc.perform(post(CALCULATE_ENDPOINT)
                    .with(httpBasic("user", "password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"basePrice": 100.0}
                        """))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/forbidden"))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.detail").isString())
                .andExpect(jsonPath("$.instance").isString());
        }
    }
}

package com.bioudiamine.errors_rfc.controller;

import com.bioudiamine.errors_rfc.exception.GlobalExceptionHandler;
import com.bioudiamine.errors_rfc.exception.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalesController.class)
@Import(GlobalExceptionHandler.class)
class SalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void calculate_withNoDiscount_returnsBasePriceAsSellingPrice() throws Exception {
        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.basePrice").value(100.0))
            .andExpect(jsonPath("$.discount").doesNotExist())
            .andExpect(jsonPath("$.sellingPrice").value(100.0));
    }

    @Test
    void calculate_withValidDiscount_returnsCorrectSellingPrice() throws Exception {
        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0, "discount": 20.0}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.basePrice").value(100.0))
            .andExpect(jsonPath("$.discount").value(20.0))
            .andExpect(jsonPath("$.sellingPrice").value(80.0));
    }

    @Test
    void calculate_withMaxAllowedDiscount_returnsCorrectSellingPrice() throws Exception {
        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 200.0, "discount": 30.0}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.basePrice").value(200.0))
            .andExpect(jsonPath("$.discount").value(30.0))
            .andExpect(jsonPath("$.sellingPrice").value(140.0));
    }

    @Test
    void calculate_withDiscountExceeding30Percent_returnsBadRequest() throws Exception {
        when(messageService.getMessage("BRE-C1-0002"))
            .thenReturn("Discount greater than 30% not allowed.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0, "discount": 50.0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0002"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Discount greater than 30% not allowed."))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0002"))
            .andExpect(jsonPath("$.errors[0].message").value("Discount greater than 30% not allowed."));
    }

    @Test
    void calculate_withDiscount100Percent_returnsBadRequestFreeSaleNotAllowed() throws Exception {
        when(messageService.getMessage("BRE-C1-0001"))
            .thenReturn("Free sale is not allowed.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0, "discount": 100.0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0001"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Free sale is not allowed."))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0001"));
    }

    @Test
    void calculate_withDiscountOver100Percent_returnsBadRequestFreeSaleNotAllowed() throws Exception {
        when(messageService.getMessage("BRE-C1-0001"))
            .thenReturn("Free sale is not allowed.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0, "discount": 150.0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/BRE-C1-0001"))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C1-0001"));
    }

    @Test
    void calculate_withNullBasePrice_returnsBadRequestValidationError() throws Exception {
        when(messageService.getMessage("BRE-C2-0001"))
            .thenReturn("Base price should be greater than zero.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"discount": 10.0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation-error"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value("Validation failed"))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C2-0001"))
            .andExpect(jsonPath("$.errors[0].field").value("basePrice"));
    }

    @Test
    void calculate_withNegativeBasePrice_returnsBadRequestValidationError() throws Exception {
        when(messageService.getMessage("BRE-C2-0001"))
            .thenReturn("Base price should be greater than zero.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": -50.0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation-error"))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C2-0001"))
            .andExpect(jsonPath("$.errors[0].field").value("basePrice"));
    }

    @Test
    void calculate_withZeroBasePrice_returnsBadRequestValidationError() throws Exception {
        when(messageService.getMessage("BRE-C2-0001"))
            .thenReturn("Base price should be greater than zero.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation-error"))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C2-0001"))
            .andExpect(jsonPath("$.errors[0].field").value("basePrice"));
    }

    @Test
    void calculate_withNegativeDiscount_returnsBadRequestValidationError() throws Exception {
        when(messageService.getMessage("BRE-C2-0002"))
            .thenReturn("Discount should be greater than zero when provided.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0, "discount": -10.0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation-error"))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C2-0002"))
            .andExpect(jsonPath("$.errors[0].field").value("discount"));
    }

    @Test
    void calculate_withZeroDiscount_returnsBadRequestValidationError() throws Exception {
        when(messageService.getMessage("BRE-C2-0002"))
            .thenReturn("Discount should be greater than zero when provided.");

        mockMvc.perform(post("/sales/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"basePrice": 100.0, "discount": 0}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation-error"))
            .andExpect(jsonPath("$.errors[0].code").value("BRE-C2-0002"))
            .andExpect(jsonPath("$.errors[0].field").value("discount"));
    }
}

package com.bioudiamine.errors_rfc.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageService messageService;

    @Test
    void getMessage_withValidCode_returnsMessage() {
        when(messageSource.getMessage("BRE-C1-0001", null, Locale.getDefault()))
            .thenReturn("Free sale is not allowed.");

        String message = messageService.getMessage("BRE-C1-0001");

        assertThat(message).isEqualTo("Free sale is not allowed.");
    }

    @Test
    void getMessage_withAnotherValidCode_returnsCorrectMessage() {
        when(messageSource.getMessage("BRE-C2-0001", null, Locale.getDefault()))
            .thenReturn("Base price should be greater than zero.");

        String message = messageService.getMessage("BRE-C2-0001");

        assertThat(message).isEqualTo("Base price should be greater than zero.");
    }

    @Test
    void getMessage_withMissingCode_throwsIllegalArgumentException() {
        when(messageSource.getMessage("UNKNOWN-CODE", null, Locale.getDefault()))
            .thenThrow(new NoSuchMessageException("UNKNOWN-CODE"));

        assertThatThrownBy(() -> messageService.getMessage("UNKNOWN-CODE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message not found for code: UNKNOWN-CODE")
            .hasCauseInstanceOf(NoSuchMessageException.class);
    }

    @Test
    void getMessage_withNullCode_throwsIllegalArgumentException() {
        when(messageSource.getMessage(null, null, Locale.getDefault()))
            .thenThrow(new NoSuchMessageException("null"));

        assertThatThrownBy(() -> messageService.getMessage(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message not found for code: null")
            .hasCauseInstanceOf(NoSuchMessageException.class);
    }

    @Test
    void getMessage_withEmptyCode_throwsIllegalArgumentException() {
        when(messageSource.getMessage("", null, Locale.getDefault()))
            .thenThrow(new NoSuchMessageException(""));

        assertThatThrownBy(() -> messageService.getMessage(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message not found for code: ")
            .hasCauseInstanceOf(NoSuchMessageException.class);
    }
}

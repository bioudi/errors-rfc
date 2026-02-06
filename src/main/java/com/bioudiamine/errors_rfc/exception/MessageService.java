package com.bioudiamine.errors_rfc.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, Locale.getDefault());
        } catch (NoSuchMessageException e) {
            throw new IllegalArgumentException("Message not found for code: " + code, e);
        }
    }
}

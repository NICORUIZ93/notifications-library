package com.notifications.exception;

import com.notifications.core.NotificationChannel;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Excepción lanzada cuando la validación de notificación falla.
 * Contiene información detallada sobre qué reglas de validación fueron violadas.
 */
@Getter
public class ValidationException extends NotificationException {

    private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
        this.validationErrors = Collections.emptyList();
    }

    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors != null ? validationErrors : Collections.emptyList();
    }

    public ValidationException(String message, NotificationChannel channel, String providerName,
                                List<String> validationErrors) {
        super(message, channel, providerName);
        this.validationErrors = validationErrors != null ? validationErrors : Collections.emptyList();
    }

    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
}

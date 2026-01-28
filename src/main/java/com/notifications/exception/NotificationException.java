package com.notifications.exception;

import com.notifications.core.NotificationChannel;
import lombok.Getter;

/**
 * Excepción base para todos los errores relacionados con notificaciones.
 * Proporciona contexto sobre qué canal y proveedor causó el error.
 */
@Getter
public class NotificationException extends RuntimeException {

    private final NotificationChannel channel;
    private final String providerName;
    private final String errorCode;

    public NotificationException(String message) {
        super(message);
        this.channel = null;
        this.providerName = null;
        this.errorCode = null;
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
        this.channel = null;
        this.providerName = null;
        this.errorCode = null;
    }

    public NotificationException(String message, NotificationChannel channel, String providerName) {
        super(message);
        this.channel = channel;
        this.providerName = providerName;
        this.errorCode = null;
    }

    public NotificationException(String message, NotificationChannel channel, String providerName, String errorCode) {
        super(message);
        this.channel = channel;
        this.providerName = providerName;
        this.errorCode = errorCode;
    }

    public NotificationException(String message, NotificationChannel channel, String providerName, Throwable cause) {
        super(message, cause);
        this.channel = channel;
        this.providerName = providerName;
        this.errorCode = null;
    }
}

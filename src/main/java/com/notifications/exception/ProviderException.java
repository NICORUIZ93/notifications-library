package com.notifications.exception;

import com.notifications.core.NotificationChannel;

/**
 * Excepción lanzada cuando un proveedor encuentra un error durante el envío.
 * Típicamente representa errores del servicio externo (errores de API, timeouts, etc.)
 */
public class ProviderException extends NotificationException {

    public ProviderException(String message) {
        super(message);
    }

    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderException(String message, NotificationChannel channel, String providerName) {
        super(message, channel, providerName);
    }

    public ProviderException(String message, NotificationChannel channel, String providerName, String errorCode) {
        super(message, channel, providerName, errorCode);
    }

    public ProviderException(String message, NotificationChannel channel, String providerName, Throwable cause) {
        super(message, channel, providerName, cause);
    }
}

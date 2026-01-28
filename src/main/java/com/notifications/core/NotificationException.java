package com.notifications.core;

import lombok.Getter;

/**
 * Excepción personalizada para errores en el sistema de notificaciones.
 * Incluye información contextual sobre el tipo de error y canal afectado.
 */
@Getter
public class NotificationException extends Exception {

    private final ErrorType errorType;
    private final ChannelType channelType;

    public NotificationException(String message, ErrorType errorType, ChannelType channelType) {
        super(message);
        this.errorType = errorType;
        this.channelType = channelType;
    }

    public NotificationException(String message, ErrorType errorType, ChannelType channelType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.channelType = channelType;
    }

    /**
     * Tipos de error posibles en el sistema de notificaciones.
     */
    public enum ErrorType {
        /** Error en la validación de datos de entrada */
        VALIDATION_ERROR,
        /** Error en la configuración del servicio */
        CONFIGURATION_ERROR,
        /** Error durante el proceso de envío */
        SEND_ERROR,
        /** Error reportado por el proveedor externo */
        PROVIDER_ERROR
    }
}

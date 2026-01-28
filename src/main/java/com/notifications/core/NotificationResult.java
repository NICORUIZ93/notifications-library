package com.notifications.core;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

/**
 * Representa el resultado de un intento de envío de notificación.
 * Usa el patrón Result para encapsular información de éxito/fallo.
 *
 * Decisión de Diseño: En lugar de lanzar excepciones para cada fallo,
 * retornamos un objeto resultado que contiene información detallada sobre
 * lo que ocurrió. Esto hace el manejo de errores más explícito y testeable.
 */
@Getter
@Builder
public class NotificationResult {

    /**
     * Si la notificación fue enviada exitosamente
     */
    private final boolean success;

    /**
     * Identificador único asignado por el proveedor (si está disponible)
     */
    private final String messageId;

    /**
     * El canal usado para enviar la notificación
     */
    private final NotificationChannel channel;

    /**
     * El proveedor que manejó la notificación
     */
    private final String providerName;

    /**
     * Marca de tiempo cuando la notificación fue procesada
     */
    private final Instant timestamp;

    /**
     * Mensaje de error si la notificación falló
     */
    private final String errorMessage;

    /**
     * Código de error del proveedor (si está disponible)
     */
    private final String errorCode;

    /**
     * Crea un resultado exitoso
     */
    public static NotificationResult success(NotificationChannel channel, String providerName, String messageId) {
        return NotificationResult.builder()
                .success(true)
                .channel(channel)
                .providerName(providerName)
                .messageId(messageId)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Crea un resultado de fallo
     */
    public static NotificationResult failure(NotificationChannel channel, String providerName,
                                              String errorMessage, String errorCode) {
        return NotificationResult.builder()
                .success(false)
                .channel(channel)
                .providerName(providerName)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Crea un resultado de fallo desde una excepción
     */
    public static NotificationResult failure(NotificationChannel channel, String providerName, Exception e) {
        return NotificationResult.builder()
                .success(false)
                .channel(channel)
                .providerName(providerName)
                .errorMessage(e.getMessage())
                .errorCode(e.getClass().getSimpleName())
                .timestamp(Instant.now())
                .build();
    }

    public Optional<String> getMessageId() {
        return Optional.ofNullable(messageId);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public Optional<String> getErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    public boolean isFailure() {
        return !success;
    }
}

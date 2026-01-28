package com.notifications.core;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Representa el resultado del envío de una notificación.
 * Proporciona métodos de fábrica para crear resultados exitosos o fallidos.
 */
@Getter
@Builder
public class NotificationResult {

    private final boolean success;
    private final String notificationId;
    private final ChannelType channelType;
    private final String message;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String providerMessageId;

    /**
     * Crea un resultado exitoso.
     *
     * @param notificationId    Identificador de la notificación
     * @param channelType       Tipo de canal utilizado
     * @param providerMessageId Identificador asignado por el proveedor
     * @return Resultado exitoso
     */
    public static NotificationResult success(String notificationId,
                                             ChannelType channelType,
                                             String providerMessageId) {
        return NotificationResult.builder()
                .success(true)
                .notificationId(notificationId)
                .channelType(channelType)
                .message("Notificación enviada exitosamente")
                .providerMessageId(providerMessageId)
                .build();
    }

    /**
     * Crea un resultado fallido.
     *
     * @param notificationId Identificador de la notificación
     * @param channelType    Tipo de canal utilizado
     * @param errorMessage   Mensaje de error
     * @return Resultado fallido
     */
    public static NotificationResult failure(String notificationId,
                                             ChannelType channelType,
                                             String errorMessage) {
        return NotificationResult.builder()
                .success(false)
                .notificationId(notificationId)
                .channelType(channelType)
                .message(errorMessage)
                .build();
    }
}

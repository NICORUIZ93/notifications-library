package com.notifications.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;
import java.util.Set;

/**
 * Modelo que representa una notificación genérica.
 * Utiliza el patrón Builder para facilitar la construcción de instancias inmutables.
 */
@Getter
@Builder
public class Notification {

    /**
     * Identificador único de la notificación.
     */
    private final String id;

    /**
     * Conjunto de destinatarios.
     */
    @Singular
    private final Set<String> recipients;

    /**
     * Contenido del mensaje.
     */
    private final String content;

    /**
     * Asunto de la notificación (aplicable a canales como email).
     */
    private final String subject;

    /**
     * Metadatos adicionales específicos del canal.
     */
    @Singular("metadata")
    private final Map<String, Object> metadata;

    /**
     * Prioridad de la notificación.
     */
    @Builder.Default
    private final Priority priority = Priority.NORMAL;

    /**
     * Canal preferido para el envío.
     */
    private final ChannelType preferredChannel;

    /**
     * Niveles de prioridad disponibles.
     */
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}

package com.notifications.model;

import com.notifications.core.NotificationChannel;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Notificación push para dispositivos móviles.
 *
 * Decisión de Diseño: Las notificaciones push tienen campos específicos como título,
 * token de dispositivo, contador de badge y payload de datos personalizados que son
 * únicos de este canal.
 */
@Getter
public class PushNotification extends BaseNotification {

    private final String title;
    private final String imageUrl;
    private final Integer badge;
    private final String sound;
    private final Map<String, String> data; // Payload de datos personalizados
    private final String topic; // Tema iOS / Canal Android
    private final Priority priority;
    private final Integer ttlSeconds; // Tiempo de vida

    @Builder
    public PushNotification(String recipient, String message, Map<String, Object> metadata,
                            String title, String imageUrl, Integer badge, String sound,
                            Map<String, String> data, String topic, Priority priority,
                            Integer ttlSeconds) {
        super(recipient, message, metadata);
        this.title = title;
        this.imageUrl = imageUrl;
        this.badge = badge;
        this.sound = sound;
        this.data = data;
        this.topic = topic;
        this.priority = priority != null ? priority : Priority.NORMAL;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    /**
     * @return El token de dispositivo (token FCM, token APNs, etc.)
     */
    public String getDeviceToken() {
        return getRecipient();
    }

    /**
     * @return El cuerpo de la notificación
     */
    public String getBody() {
        return getMessage();
    }

    /**
     * Niveles de prioridad de notificación push
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }
}

package com.notifications.model;

import com.notifications.core.Notification;
import com.notifications.core.NotificationChannel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase base abstracta para todos los tipos de notificación.
 * Proporciona funcionalidad común mientras permite extensiones específicas por canal.
 */
@Getter
@SuperBuilder
public abstract class BaseNotification implements Notification {

    protected final String recipient;
    protected final String message;
    protected final Map<String, Object> metadata;

    protected BaseNotification(String recipient, String message, Map<String, Object> metadata) {
        this.recipient = recipient;
        this.message = message;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    public abstract NotificationChannel getChannel();
}

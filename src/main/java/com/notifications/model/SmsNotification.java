package com.notifications.model;

import com.notifications.core.NotificationChannel;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Notificación específica de SMS.
 *
 * Decisión de Diseño: SMS es más simple que email - solo un número de teléfono y mensaje.
 * Incluimos sender ID para proveedores que soportan IDs de remitente alfanuméricos.
 */
@Getter
public class SmsNotification extends BaseNotification {

    private final String from;
    private final String senderId; // ID de remitente alfanumérico (si es soportado por el proveedor)

    @Builder
    public SmsNotification(String recipient, String message, Map<String, Object> metadata,
                           String from, String senderId) {
        super(recipient, message, metadata);
        this.from = from;
        this.senderId = senderId;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    /**
     * @return El número de teléfono al que enviar el SMS
     */
    public String getPhoneNumber() {
        return getRecipient();
    }
}

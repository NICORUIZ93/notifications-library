package com.notifications.channels.push;

import com.notifications.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Canal para el envío de notificaciones push.
 * Soporta envío a dispositivos móviles mediante tokens de dispositivo.
 */
@Slf4j
@RequiredArgsConstructor
public class PushChannel implements NotificationChannel {

    private final PushProvider provider;

    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        validateNotification(notification);

        try {
            log.info("Enviando notificación push: {}", notification.getId());

            String title = notification.getSubject() != null ?
                    notification.getSubject() : "Notificación";
            String body = notification.getContent();

            String providerMessageId = provider.sendPush(
                    notification.getRecipients(),
                    title,
                    body,
                    notification.getMetadata()
            );

            return NotificationResult.success(
                    notification.getId(),
                    ChannelType.PUSH,
                    providerMessageId
            );

        } catch (Exception e) {
            log.error("Error al enviar notificación push: {}", notification.getId(), e);
            throw new NotificationException(
                    "Error al enviar notificación push: " + e.getMessage(),
                    NotificationException.ErrorType.SEND_ERROR,
                    ChannelType.PUSH,
                    e
            );
        }
    }

    @Override
    public boolean supports(Notification notification) {
        return !notification.getRecipients().isEmpty();
    }

    @Override
    public ChannelType getType() {
        return ChannelType.PUSH;
    }

    private void validateNotification(Notification notification) throws NotificationException {
        if (notification.getRecipients().isEmpty()) {
            throw new NotificationException(
                    "Se requiere al menos un destinatario para enviar notificación push",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.PUSH
            );
        }

        if (notification.getContent() == null || notification.getContent().trim().isEmpty()) {
            throw new NotificationException(
                    "El contenido de la notificación push no puede estar vacío",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.PUSH
            );
        }
    }
}

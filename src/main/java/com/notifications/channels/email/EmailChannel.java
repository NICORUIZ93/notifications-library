package com.notifications.channels.email;

import com.notifications.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Canal para el envío de notificaciones por correo electrónico.
 * Implementa validación de direcciones y delegación al proveedor configurado.
 */
@Slf4j
@RequiredArgsConstructor
public class EmailChannel implements NotificationChannel {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EmailProvider provider;

    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        validateNotification(notification);

        try {
            log.info("Enviando notificación por email: {}", notification.getId());

            String subject = notification.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                subject = "Notificación";
            }

            String providerMessageId = provider.sendEmail(
                    notification.getRecipients(),
                    subject,
                    notification.getContent(),
                    notification.getMetadata()
            );

            return NotificationResult.success(
                    notification.getId(),
                    ChannelType.EMAIL,
                    providerMessageId
            );

        } catch (Exception e) {
            log.error("Error al enviar notificación por email: {}", notification.getId(), e);
            throw new NotificationException(
                    "Error al enviar email: " + e.getMessage(),
                    NotificationException.ErrorType.SEND_ERROR,
                    ChannelType.EMAIL,
                    e
            );
        }
    }

    @Override
    public boolean supports(Notification notification) {
        return notification.getRecipients().stream()
                .anyMatch(this::isValidEmail);
    }

    @Override
    public ChannelType getType() {
        return ChannelType.EMAIL;
    }

    private void validateNotification(Notification notification) throws NotificationException {
        if (notification.getRecipients().isEmpty()) {
            throw new NotificationException(
                    "Se requiere al menos un destinatario para enviar email",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.EMAIL
            );
        }

        boolean hasValidEmail = notification.getRecipients().stream()
                .anyMatch(this::isValidEmail);

        if (!hasValidEmail) {
            throw new NotificationException(
                    "No se encontraron direcciones de email válidas en los destinatarios",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.EMAIL
            );
        }

        if (notification.getContent() == null || notification.getContent().trim().isEmpty()) {
            throw new NotificationException(
                    "El contenido del email no puede estar vacío",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.EMAIL
            );
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}

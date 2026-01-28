package com.notifications.channels.sms;

import com.notifications.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Canal para el envío de notificaciones por SMS.
 * Implementa validación de números telefónicos y límite de caracteres.
 */
@Slf4j
@RequiredArgsConstructor
public class SmsChannel implements NotificationChannel {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final int SMS_MAX_LENGTH = 160;

    private final SmsProvider provider;

    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        validateNotification(notification);

        try {
            log.info("Enviando notificación por SMS: {}", notification.getId());

            String message = notification.getContent();
            if (message == null || message.trim().isEmpty()) {
                message = "Notificación";
            }

            if (message.length() > SMS_MAX_LENGTH) {
                message = message.substring(0, SMS_MAX_LENGTH - 3) + "...";
                log.warn("Mensaje SMS truncado a {} caracteres", SMS_MAX_LENGTH);
            }

            String providerMessageId = provider.sendSms(
                    notification.getRecipients(),
                    message,
                    notification.getMetadata()
            );

            return NotificationResult.success(
                    notification.getId(),
                    ChannelType.SMS,
                    providerMessageId
            );

        } catch (Exception e) {
            log.error("Error al enviar notificación por SMS: {}", notification.getId(), e);
            throw new NotificationException(
                    "Error al enviar SMS: " + e.getMessage(),
                    NotificationException.ErrorType.SEND_ERROR,
                    ChannelType.SMS,
                    e
            );
        }
    }

    @Override
    public boolean supports(Notification notification) {
        return notification.getRecipients().stream()
                .anyMatch(this::isValidPhoneNumber);
    }

    @Override
    public ChannelType getType() {
        return ChannelType.SMS;
    }

    private void validateNotification(Notification notification) throws NotificationException {
        if (notification.getRecipients().isEmpty()) {
            throw new NotificationException(
                    "Se requiere al menos un destinatario para enviar SMS",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.SMS
            );
        }

        boolean hasValidPhone = notification.getRecipients().stream()
                .anyMatch(this::isValidPhoneNumber);

        if (!hasValidPhone) {
            throw new NotificationException(
                    "No se encontraron números telefónicos válidos en los destinatarios",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.SMS
            );
        }

        if (notification.getContent() == null || notification.getContent().trim().isEmpty()) {
            throw new NotificationException(
                    "El contenido del SMS no puede estar vacío",
                    NotificationException.ErrorType.VALIDATION_ERROR,
                    ChannelType.SMS
            );
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}

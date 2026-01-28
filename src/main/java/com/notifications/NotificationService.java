package com.notifications;

import com.notifications.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio principal para el envío de notificaciones.
 * Implementa los patrones Facade y Builder para simplificar la interacción con múltiples canales.
 */
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final Map<ChannelType, NotificationChannel> channels;

    /**
     * Envía una notificación utilizando el canal apropiado.
     *
     * @param notification Notificación a enviar
     * @return Resultado del envío
     * @throws NotificationException Si ocurre un error durante el proceso
     */
    public NotificationResult send(Notification notification) throws NotificationException {
        log.info("Procesando notificación: {}", notification.getId());

        if (channels.isEmpty()) {
            throw new NotificationException(
                    "No hay canales de notificación configurados",
                    NotificationException.ErrorType.CONFIGURATION_ERROR,
                    null
            );
        }

        NotificationChannel channel = selectChannel(notification);
        return channel.send(notification);
    }

    /**
     * Envía una notificación de manera asíncrona.
     *
     * @param notification Notificación a enviar
     * @return CompletableFuture con el resultado del envío
     */
    public CompletableFuture<NotificationResult> sendAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(notification);
            } catch (NotificationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Envía múltiples notificaciones en lote.
     *
     * @param notifications Lista de notificaciones a enviar
     * @return Lista de resultados correspondientes a cada notificación
     */
    public List<NotificationResult> sendBatch(List<Notification> notifications) {
        List<NotificationResult> results = new ArrayList<>();

        for (Notification notification : notifications) {
            try {
                NotificationResult result = send(notification);
                results.add(result);
            } catch (NotificationException e) {
                log.error("Error al enviar notificación en lote: {}", notification.getId(), e);
                results.add(NotificationResult.failure(
                        notification.getId(),
                        e.getChannelType(),
                        e.getMessage()
                ));
            }
        }

        return results;
    }

    /**
     * Selecciona el canal apropiado para la notificación.
     * Prioriza el canal preferido si está disponible, de lo contrario selecciona automáticamente.
     */
    private NotificationChannel selectChannel(Notification notification) throws NotificationException {
        if (notification.getPreferredChannel() != null) {
            NotificationChannel preferredChannel = channels.get(notification.getPreferredChannel());
            if (preferredChannel != null && preferredChannel.supports(notification)) {
                return preferredChannel;
            }

            log.warn("Canal preferido {} no disponible, seleccionando automáticamente",
                    notification.getPreferredChannel());
        }

        Optional<NotificationChannel> compatibleChannel = channels.values().stream()
                .filter(channel -> channel.supports(notification))
                .findFirst();

        if (compatibleChannel.isPresent()) {
            return compatibleChannel.get();
        }

        throw new NotificationException(
                "No se encontró un canal compatible para los destinatarios especificados",
                NotificationException.ErrorType.VALIDATION_ERROR,
                null
        );
    }

    /**
     * Builder para la construcción fluida del servicio.
     */
    public static class Builder {
        private final Map<ChannelType, NotificationChannel> channels = new HashMap<>();

        public Builder withEmailChannel(com.notifications.channels.email.EmailProvider emailProvider) {
            channels.put(ChannelType.EMAIL,
                    new com.notifications.channels.email.EmailChannel(emailProvider));
            return this;
        }

        public Builder withSmsChannel(com.notifications.channels.sms.SmsProvider smsProvider) {
            channels.put(ChannelType.SMS,
                    new com.notifications.channels.sms.SmsChannel(smsProvider));
            return this;
        }

        public Builder withPushChannel(com.notifications.channels.push.PushProvider pushProvider) {
            channels.put(ChannelType.PUSH,
                    new com.notifications.channels.push.PushChannel(pushProvider));
            return this;
        }

        public Builder withCustomChannel(ChannelType type, NotificationChannel channel) {
            channels.put(type, channel);
            return this;
        }

        public NotificationService build() {
            if (channels.isEmpty()) {
                throw new IllegalStateException("Debe configurarse al menos un canal");
            }
            return new NotificationService(channels);
        }
    }
}

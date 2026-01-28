package com.notifications.core;

import java.util.concurrent.CompletableFuture;

/**
 * Interfaz principal para enviar notificaciones.
 * Esta es la abstracción principal con la que los clientes interactúan.
 *
 * Patrón de Diseño: Strategy Pattern - Diferentes implementaciones pueden ser
 * intercambiadas sin cambiar el código cliente.
 *
 * SOLID: Principio de Inversión de Dependencias - Los módulos de alto nivel dependen
 * de esta abstracción, no de implementaciones concretas.
 */
public interface NotificationSender {

    /**
     * Envía una notificación de forma síncrona.
     *
     * @param notification La notificación a enviar
     * @return El resultado de la operación de envío
     */
    NotificationResult send(Notification notification);

    /**
     * Envía una notificación de forma asíncrona.
     *
     * @param notification La notificación a enviar
     * @return Un CompletableFuture conteniendo el resultado
     */
    CompletableFuture<NotificationResult> sendAsync(Notification notification);

    /**
     * @return El canal que este sender maneja
     */
    NotificationChannel getChannel();

    /**
     * @return Si este sender soporta el tipo de notificación dado
     */
    boolean supports(Notification notification);
}

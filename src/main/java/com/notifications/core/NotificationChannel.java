package com.notifications.core;

/**
 * Interfaz base para todos los canales de notificación.
 * Define el contrato que deben implementar los canales específicos.
 */
public interface NotificationChannel {

    /**
     * Envía una notificación a través de este canal.
     *
     * @param notification Notificación a enviar
     * @return Resultado del envío
     * @throws NotificationException Si ocurre un error durante el envío
     */
    NotificationResult send(Notification notification) throws NotificationException;

    /**
     * Verifica si este canal puede procesar la notificación especificada.
     *
     * @param notification Notificación a evaluar
     * @return true si el canal puede procesar la notificación
     */
    boolean supports(Notification notification);

    /**
     * Obtiene el tipo de canal.
     *
     * @return Tipo de canal
     */
    ChannelType getType();
}

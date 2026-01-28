package com.notifications.core;

import java.util.Map;

/**
 * Interfaz principal que representa una notificación a enviar.
 * Este es el contrato base que todos los tipos de notificación deben implementar.
 *
 * Decisión de Diseño: Usar una interfaz permite que diferentes tipos de notificación
 * tengan sus propios campos específicos mientras mantienen un contrato común.
 */
public interface Notification {

    /**
     * @return El destinatario de la notificación (email, número de teléfono, token de dispositivo, etc.)
     */
    String getRecipient();

    /**
     * @return El contenido/cuerpo principal de la notificación
     */
    String getMessage();

    /**
     * @return El canal a través del cual se debe enviar esta notificación
     */
    NotificationChannel getChannel();

    /**
     * @return Metadatos adicionales para la notificación (opcional)
     */
    Map<String, Object> getMetadata();
}

package com.notifications.channels.push;

import java.util.Map;
import java.util.Set;

/**
 * Interfaz para proveedores de notificaciones push.
 * Implementa el patrón Strategy para permitir intercambio de proveedores.
 */
public interface PushProvider {

    /**
     * Envía una notificación push.
     *
     * @param deviceTokens Conjunto de tokens de dispositivo destinatarios
     * @param title        Título de la notificación
     * @param body         Cuerpo del mensaje
     * @param metadata     Metadatos adicionales
     * @return Identificador del mensaje asignado por el proveedor
     * @throws Exception Si ocurre un error durante el envío
     */
    String sendPush(
            Set<String> deviceTokens,
            String title,
            String body,
            Map<String, Object> metadata
    ) throws Exception;
}

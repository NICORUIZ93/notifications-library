package com.notifications.channels.sms;

import java.util.Map;
import java.util.Set;

/**
 * Interfaz para proveedores de SMS.
 * Implementa el patrón Strategy para permitir intercambio de proveedores.
 */
public interface SmsProvider {

    /**
     * Envía un mensaje SMS.
     *
     * @param recipients Conjunto de números telefónicos destinatarios
     * @param message    Contenido del mensaje
     * @param metadata   Metadatos adicionales
     * @return Identificador del mensaje asignado por el proveedor
     * @throws Exception Si ocurre un error durante el envío
     */
    String sendSms(
            Set<String> recipients,
            String message,
            Map<String, Object> metadata
    ) throws Exception;
}

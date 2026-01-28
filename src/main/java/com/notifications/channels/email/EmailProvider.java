package com.notifications.channels.email;

import java.util.Map;
import java.util.Set;

/**
 * Interfaz para proveedores de correo electrónico.
 * Implementa el patrón Strategy para permitir intercambio de proveedores.
 */
public interface EmailProvider {

    /**
     * Envía un correo electrónico.
     *
     * @param recipients Conjunto de destinatarios
     * @param subject    Asunto del correo
     * @param content    Contenido del mensaje
     * @param metadata   Metadatos adicionales
     * @return Identificador del mensaje asignado por el proveedor
     * @throws Exception Si ocurre un error durante el envío
     */
    String sendEmail(
            Set<String> recipients,
            String subject,
            String content,
            Map<String, Object> metadata
    ) throws Exception;
}

package com.notifications.core;

/**
 * Interfaz para proveedores de notificaciones (SendGrid, Twilio, Firebase, etc.).
 * Cada proveedor implementa la lógica real de envío para un servicio específico.
 *
 * Patrón de Diseño: Strategy Pattern - Permite intercambiar proveedores sin
 * cambiar la implementación del canal.
 *
 * SOLID: Responsabilidad Única - Cada proveedor solo maneja comunicación
 * con un servicio externo.
 *
 * SOLID: Abierto/Cerrado - Nuevos proveedores pueden ser agregados sin modificar
 * código existente.
 */
public interface NotificationProvider {

    /**
     * Envía una notificación a través de este proveedor.
     *
     * @param notification La notificación a enviar
     * @return El resultado de la operación de envío
     */
    NotificationResult send(Notification notification);

    /**
     * @return El nombre único de este proveedor (ej: "sendgrid", "twilio")
     */
    String getName();

    /**
     * @return El canal que este proveedor soporta
     */
    NotificationChannel getChannel();

    /**
     * Valida la notificación antes de enviar.
     *
     * @param notification La notificación a validar
     * @throws com.notifications.exception.ValidationException si la validación falla
     */
    void validate(Notification notification);

    /**
     * @return Si este proveedor está correctamente configurado y listo para enviar
     */
    boolean isConfigured();
}

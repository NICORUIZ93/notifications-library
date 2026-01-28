package com.notifications.exception;

/**
 * Excepción lanzada cuando hay un error de configuración.
 * Incluye API keys faltantes, valores de configuración inválidos, etc.
 */
public class ConfigurationException extends NotificationException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

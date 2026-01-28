package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.Notification;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationProvider;
import com.notifications.core.NotificationResult;
import com.notifications.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase base abstracta para todos los proveedores.
 * Proporciona funcionalidad común como validación y construcción de resultados.
 *
 * Patrón de Diseño: Template Method - Define el esqueleto del algoritmo de envío,
 * con pasos específicos implementados por las subclases.
 */
public abstract class AbstractProvider implements NotificationProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ProviderConfig config;

    protected AbstractProvider(ProviderConfig config) {
        this.config = config;
    }

    @Override
    public NotificationResult send(Notification notification) {
        validate(notification);
        return doSend(notification);
    }

    /**
     * Método plantilla para la lógica real de envío.
     * Las subclases implementan esto para realizar el envío real.
     */
    protected abstract NotificationResult doSend(Notification notification);

    @Override
    public void validate(Notification notification) {
        List<String> errors = new ArrayList<>();

        if (notification.getRecipient() == null || notification.getRecipient().isBlank()) {
            errors.add("El destinatario es requerido");
        }

        // Push permite título o cuerpo, la validación específica lo maneja
        if (notification.getChannel() != NotificationChannel.PUSH) {
            if (notification.getMessage() == null || notification.getMessage().isBlank()) {
                errors.add("El mensaje es requerido");
            }
        }

        // Validación específica del canal
        validateSpecific(notification, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(
                    "Validación fallida para proveedor " + getName(),
                    getChannel(),
                    getName(),
                    errors
            );
        }
    }

    /**
     * Método hook para validación específica del canal.
     * Las subclases pueden sobrescribir para agregar reglas de validación adicionales.
     */
    protected void validateSpecific(Notification notification, List<String> errors) {
        // Por defecto: sin validación adicional
    }

    @Override
    public boolean isConfigured() {
        return config != null;
    }

    protected String generateMessageId() {
        return UUID.randomUUID().toString();
    }

    protected NotificationResult successResult(String messageId) {
        return NotificationResult.success(getChannel(), getName(), messageId);
    }

    protected NotificationResult failureResult(String errorMessage, String errorCode) {
        return NotificationResult.failure(getChannel(), getName(), errorMessage, errorCode);
    }

    protected NotificationResult failureResult(Exception e) {
        return NotificationResult.failure(getChannel(), getName(), e);
    }
}

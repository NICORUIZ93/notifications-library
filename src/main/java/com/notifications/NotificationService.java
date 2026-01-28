package com.notifications;

import com.notifications.config.NotificationConfig;
import com.notifications.core.*;
import com.notifications.exception.ConfigurationException;
import com.notifications.exception.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Punto de entrada principal para la librería de notificaciones.
 * Proporciona una API unificada para enviar notificaciones a través de todos los canales.
 *
 * Patrón de Diseño: Facade Pattern - Proporciona una interfaz simple al
 * subsistema complejo de proveedores y canales.
 *
 * Ejemplo de uso:
 * <pre>
 * NotificationService service = NotificationService.builder()
 *     .config(config)
 *     .register(new SendGridProvider(config))
 *     .register(new TwilioProvider(config))
 *     .register(new FirebaseProvider(config))
 *     .build();
 *
 * NotificationResult result = service.send(EmailNotification.builder()
 *     .recipient("usuario@ejemplo.com")
 *     .subject("Hola")
 *     .message("Mundo")
 *     .build());
 * </pre>
 */
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationConfig config;
    private final ProviderRegistry registry;
    private final ExecutorService executorService;

    private NotificationService(Builder builder) {
        this.config = builder.config;
        this.registry = builder.registry;
        this.executorService = builder.executorService != null
                ? builder.executorService
                : Executors.newFixedThreadPool(config != null ? config.getThreadPoolSize() : 10);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Envía una notificación de forma síncrona usando el proveedor por defecto del canal.
     *
     * @param notification La notificación a enviar
     * @return El resultado de la operación de envío
     */
    public NotificationResult send(Notification notification) {
        NotificationProvider provider = getProviderForNotification(notification);
        return sendWithProvider(notification, provider);
    }

    /**
     * Envía una notificación de forma síncrona usando un proveedor específico.
     *
     * @param notification La notificación a enviar
     * @param providerName El nombre del proveedor a usar
     * @return El resultado de la operación de envío
     */
    public NotificationResult send(Notification notification, String providerName) {
        NotificationProvider provider = registry.getProvider(notification.getChannel(), providerName)
                .orElseThrow(() -> new ConfigurationException(
                        "Proveedor '" + providerName + "' no encontrado para canal " + notification.getChannel()));
        return sendWithProvider(notification, provider);
    }

    /**
     * Envía una notificación de forma asíncrona usando el proveedor por defecto.
     *
     * @param notification La notificación a enviar
     * @return Un CompletableFuture con el resultado
     */
    public CompletableFuture<NotificationResult> sendAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> send(notification), executorService);
    }

    /**
     * Envía una notificación de forma asíncrona usando un proveedor específico.
     *
     * @param notification La notificación a enviar
     * @param providerName El proveedor a usar
     * @return Un CompletableFuture con el resultado
     */
    public CompletableFuture<NotificationResult> sendAsync(Notification notification, String providerName) {
        return CompletableFuture.supplyAsync(() -> send(notification, providerName), executorService);
    }

    /**
     * Envía múltiples notificaciones de forma asíncrona.
     *
     * @param notifications Las notificaciones a enviar
     * @return Un CompletableFuture con todos los resultados
     */
    public CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications) {
        List<CompletableFuture<NotificationResult>> futures = notifications.stream()
                .map(this::sendAsync)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    private NotificationProvider getProviderForNotification(Notification notification) {
        return registry.getDefaultProvider(notification.getChannel())
                .orElseThrow(() -> new ConfigurationException(
                        "No hay proveedor configurado para el canal: " + notification.getChannel()));
    }

    private NotificationResult sendWithProvider(Notification notification, NotificationProvider provider) {
        log.debug("Enviando notificación via proveedor {}", provider.getName());

        try {
            // Validar primero
            provider.validate(notification);

            // Enviar
            NotificationResult result = provider.send(notification);

            if (result.isSuccess()) {
                log.info("Notificación enviada exitosamente via {} [messageId={}]",
                        provider.getName(), result.getMessageId().orElse("N/A"));
            } else {
                log.warn("Notificación falló via {} [error={}]",
                        provider.getName(), result.getErrorMessage().orElse("Error desconocido"));
            }

            return result;

        } catch (NotificationException e) {
            log.error("Error de notificación via {}: {}", provider.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado enviando notificación via {}", provider.getName(), e);
            return NotificationResult.failure(notification.getChannel(), provider.getName(), e);
        }
    }

    /**
     * Obtiene el registro de proveedores para configuración avanzada.
     */
    public ProviderRegistry getRegistry() {
        return registry;
    }

    /**
     * Cierra el servicio de ejecución.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    public static class Builder {
        private NotificationConfig config;
        private final ProviderRegistry registry = new ProviderRegistry();
        private ExecutorService executorService;

        public Builder config(NotificationConfig config) {
            this.config = config;
            return this;
        }

        public Builder register(NotificationProvider provider) {
            registry.register(provider);
            return this;
        }

        public Builder defaultProvider(NotificationChannel channel, String providerName) {
            registry.setDefault(channel, providerName);
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public NotificationService build() {
            return new NotificationService(this);
        }
    }
}

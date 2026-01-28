package com.notifications.config;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para un proveedor de notificaciones.
 * Contiene credenciales de API y configuraciones específicas del proveedor.
 *
 * Decisión de Diseño: Usar una clase de configuración genérica permite
 * que diferentes proveedores tengan diferentes configuraciones sin crear
 * una clase de configuración separada para cada proveedor.
 */
@Getter
@Builder
public class ProviderConfig {

    private final String apiKey;
    private final String apiSecret;
    private final String baseUrl;
    private final String accountId;
    private final String authToken;
    private final int timeoutMs;
    private final int maxRetries;
    private final Map<String, Object> additionalProperties;

    public static class ProviderConfigBuilder {
        private int timeoutMs = 30000; // Por defecto 30 segundos
        private int maxRetries = 3;    // Por defecto 3 reintentos
        private Map<String, Object> additionalProperties = new HashMap<>();

        public ProviderConfigBuilder property(String key, Object value) {
            this.additionalProperties.put(key, value);
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        Object value = additionalProperties.get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public String getProperty(String key) {
        Object value = additionalProperties.get(key);
        return value != null ? value.toString() : null;
    }
}

package com.notifications.config;

import com.notifications.core.NotificationChannel;
import lombok.Getter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal de configuración para la librería de notificaciones.
 * Usa el patrón Builder para configuración fluida y legible.
 *
 * Patrón de Diseño: Builder Pattern - Permite construcción paso a paso
 * de objetos de configuración complejos.
 *
 * Ejemplo de uso:
 * <pre>
 * NotificationConfig config = NotificationConfig.builder()
 *     .emailProvider("sendgrid", ProviderConfig.builder()
 *         .apiKey("sg_xxx")
 *         .build())
 *     .smsProvider("twilio", ProviderConfig.builder()
 *         .accountId("AC_xxx")
 *         .authToken("token_xxx")
 *         .build())
 *     .build();
 * </pre>
 */
@Getter
public class NotificationConfig {

    private final Map<NotificationChannel, Map<String, ProviderConfig>> providerConfigs;
    private final Map<NotificationChannel, String> defaultProviders;
    private final boolean asyncEnabled;
    private final int threadPoolSize;

    private NotificationConfig(Builder builder) {
        this.providerConfigs = builder.providerConfigs;
        this.defaultProviders = builder.defaultProviders;
        this.asyncEnabled = builder.asyncEnabled;
        this.threadPoolSize = builder.threadPoolSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProviderConfig getProviderConfig(NotificationChannel channel, String providerName) {
        Map<String, ProviderConfig> channelProviders = providerConfigs.get(channel);
        if (channelProviders == null) {
            return null;
        }
        return channelProviders.get(providerName.toLowerCase());
    }

    public String getDefaultProvider(NotificationChannel channel) {
        return defaultProviders.get(channel);
    }

    public boolean hasProvider(NotificationChannel channel, String providerName) {
        Map<String, ProviderConfig> channelProviders = providerConfigs.get(channel);
        return channelProviders != null && channelProviders.containsKey(providerName.toLowerCase());
    }

    public static class Builder {
        private final Map<NotificationChannel, Map<String, ProviderConfig>> providerConfigs = new EnumMap<>(NotificationChannel.class);
        private final Map<NotificationChannel, String> defaultProviders = new EnumMap<>(NotificationChannel.class);
        private boolean asyncEnabled = true;
        private int threadPoolSize = 10;

        public Builder emailProvider(String providerName, ProviderConfig config) {
            return addProvider(NotificationChannel.EMAIL, providerName, config);
        }

        public Builder smsProvider(String providerName, ProviderConfig config) {
            return addProvider(NotificationChannel.SMS, providerName, config);
        }

        public Builder pushProvider(String providerName, ProviderConfig config) {
            return addProvider(NotificationChannel.PUSH, providerName, config);
        }

        public Builder addProvider(NotificationChannel channel, String providerName, ProviderConfig config) {
            providerConfigs.computeIfAbsent(channel, k -> new HashMap<>())
                    .put(providerName.toLowerCase(), config);

            // Establecer como default si es el primer proveedor para este canal
            if (!defaultProviders.containsKey(channel)) {
                defaultProviders.put(channel, providerName.toLowerCase());
            }
            return this;
        }

        public Builder defaultEmailProvider(String providerName) {
            defaultProviders.put(NotificationChannel.EMAIL, providerName.toLowerCase());
            return this;
        }

        public Builder defaultSmsProvider(String providerName) {
            defaultProviders.put(NotificationChannel.SMS, providerName.toLowerCase());
            return this;
        }

        public Builder defaultPushProvider(String providerName) {
            defaultProviders.put(NotificationChannel.PUSH, providerName.toLowerCase());
            return this;
        }

        public Builder asyncEnabled(boolean enabled) {
            this.asyncEnabled = enabled;
            return this;
        }

        public Builder threadPoolSize(int size) {
            this.threadPoolSize = size;
            return this;
        }

        public NotificationConfig build() {
            return new NotificationConfig(this);
        }
    }
}

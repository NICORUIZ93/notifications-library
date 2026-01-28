package com.notifications.core;

import com.notifications.exception.ConfigurationException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Registro de proveedores de notificaciones.
 * Gestiona el registro y búsqueda de proveedores por canal.
 *
 * Patrón de Diseño: Registry Pattern - Ubicación central para registro
 * y recuperación de proveedores.
 *
 * SOLID: Abierto/Cerrado - Nuevos proveedores pueden ser registrados sin
 * modificar esta clase.
 */
public class ProviderRegistry {

    private final Map<NotificationChannel, Map<String, NotificationProvider>> providers;
    private final Map<NotificationChannel, String> defaultProviders;

    public ProviderRegistry() {
        this.providers = new EnumMap<>(NotificationChannel.class);
        this.defaultProviders = new EnumMap<>(NotificationChannel.class);
    }

    /**
     * Registra un proveedor para un canal específico.
     *
     * @param provider El proveedor a registrar
     * @return este registro para encadenamiento
     */
    public ProviderRegistry register(NotificationProvider provider) {
        providers.computeIfAbsent(provider.getChannel(), k -> new HashMap<>())
                .put(provider.getName().toLowerCase(), provider);

        // Establecer como default si es el primer proveedor para este canal
        if (!defaultProviders.containsKey(provider.getChannel())) {
            defaultProviders.put(provider.getChannel(), provider.getName().toLowerCase());
        }

        return this;
    }

    /**
     * Establece el proveedor por defecto para un canal.
     *
     * @param channel El canal
     * @param providerName El nombre del proveedor
     * @return este registro para encadenamiento
     */
    public ProviderRegistry setDefault(NotificationChannel channel, String providerName) {
        if (!hasProvider(channel, providerName)) {
            throw new ConfigurationException(
                    "No se puede establecer por defecto: proveedor '" + providerName + "' no registrado para canal " + channel);
        }
        defaultProviders.put(channel, providerName.toLowerCase());
        return this;
    }

    /**
     * Obtiene un proveedor específico por canal y nombre.
     */
    public Optional<NotificationProvider> getProvider(NotificationChannel channel, String providerName) {
        Map<String, NotificationProvider> channelProviders = providers.get(channel);
        if (channelProviders == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(channelProviders.get(providerName.toLowerCase()));
    }

    /**
     * Obtiene el proveedor por defecto para un canal.
     */
    public Optional<NotificationProvider> getDefaultProvider(NotificationChannel channel) {
        String defaultName = defaultProviders.get(channel);
        if (defaultName == null) {
            return Optional.empty();
        }
        return getProvider(channel, defaultName);
    }

    /**
     * Verifica si un proveedor está registrado.
     */
    public boolean hasProvider(NotificationChannel channel, String providerName) {
        Map<String, NotificationProvider> channelProviders = providers.get(channel);
        return channelProviders != null && channelProviders.containsKey(providerName.toLowerCase());
    }

    /**
     * Obtiene todos los nombres de proveedores registrados para un canal.
     */
    public Set<String> getProviderNames(NotificationChannel channel) {
        Map<String, NotificationProvider> channelProviders = providers.get(channel);
        if (channelProviders == null) {
            return Set.of();
        }
        return channelProviders.keySet();
    }

    /**
     * Obtiene todos los proveedores para un canal.
     */
    public Map<String, NotificationProvider> getProviders(NotificationChannel channel) {
        return providers.getOrDefault(channel, Map.of());
    }
}

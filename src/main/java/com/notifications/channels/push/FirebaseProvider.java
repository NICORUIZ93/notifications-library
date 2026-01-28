package com.notifications.channels.push;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * Implementación simulada del proveedor Firebase Cloud Messaging para notificaciones push.
 * En producción, esta clase realizaría llamadas HTTP a la API de Firebase.
 */
@Slf4j
@RequiredArgsConstructor
public class FirebaseProvider implements PushProvider {

    private final String serviceAccountKey;

    @Override
    public String sendPush(Set<String> deviceTokens, String title, String body, Map<String, Object> metadata) {
        log.info("[FIREBASE] Enviando push a {} dispositivos", deviceTokens.size());
        log.info("[FIREBASE] Título: {}, Cuerpo: {}", title, body);
        log.debug("[FIREBASE] Service Account: {}...", serviceAccountKey.substring(0, Math.min(serviceAccountKey.length(), 8)));

        String messageId = "fcm_" + System.currentTimeMillis();
        log.info("[FIREBASE] Push enviado. ID: {}", messageId);

        return messageId;
    }
}

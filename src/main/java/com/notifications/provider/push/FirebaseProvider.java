package com.notifications.provider.push;

import com.notifications.config.ProviderConfig;
import com.notifications.core.Notification;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.model.PushNotification;
import com.notifications.provider.AbstractProvider;

import java.util.List;

/**
 * Proveedor de notificaciones push Firebase Cloud Messaging (FCM).
 *
 * Esta es una implementación simulada que demuestra la estructura
 * y validación que sería requerida para una integración real con FCM.
 *
 * La implementación real usaría el Firebase Admin SDK:
 * - com.google.firebase:firebase-admin
 * - POST https://fcm.googleapis.com/v1/projects/{project_id}/messages:send
 *
 * @see <a href="https://firebase.google.com/docs/cloud-messaging">Firebase Cloud Messaging</a>
 */
public class FirebaseProvider extends AbstractProvider {

    private static final String NAME = "firebase";
    // Los tokens FCM típicamente tienen 140-200 caracteres
    private static final int MIN_TOKEN_LENGTH = 100;
    private static final int MAX_TOKEN_LENGTH = 300;

    public FirebaseProvider(ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    protected NotificationResult doSend(Notification notification) {
        PushNotification push = (PushNotification) notification;

        log.info("[SIMULADO] Firebase enviando notificación push");
        log.debug("[SIMULADO] Título: {}", push.getTitle());
        log.debug("[SIMULADO] Cuerpo: {}", push.getBody());
        log.debug("[SIMULADO] Token: {}...", push.getDeviceToken().substring(0, Math.min(20, push.getDeviceToken().length())));

        // Simular llamada a API
        // En implementación real:
        // FirebaseOptions options = FirebaseOptions.builder()
        //     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        //     .build();
        // FirebaseApp.initializeApp(options);
        //
        // Message message = Message.builder()
        //     .setNotification(Notification.builder()
        //         .setTitle(title)
        //         .setBody(body)
        //         .build())
        //     .setToken(deviceToken)
        //     .build();
        // String response = FirebaseMessaging.getInstance().send(message);

        // Los IDs de mensaje FCM lucen como: projects/{project}/messages/{message_id}
        String projectId = config.getProperty("projectId");
        String messageId = "projects/" + (projectId != null ? projectId : "demo-project")
                + "/messages/" + generateMessageId();

        log.info("[SIMULADO] Push de Firebase enviado exitosamente. MessageId: {}", messageId);

        return successResult(messageId);
    }

    @Override
    protected void validateSpecific(Notification notification, List<String> errors) {
        if (!(notification instanceof PushNotification push)) {
            errors.add("Se esperaba PushNotification pero se recibió " + notification.getClass().getSimpleName());
            return;
        }

        // Validar token de dispositivo
        String token = push.getDeviceToken();
        if (token == null || token.isBlank()) {
            errors.add("El token de dispositivo es requerido");
        } else if (token.length() < MIN_TOKEN_LENGTH || token.length() > MAX_TOKEN_LENGTH) {
            errors.add("Longitud de token de dispositivo inválida. Se esperan entre " + MIN_TOKEN_LENGTH
                    + " y " + MAX_TOKEN_LENGTH + " caracteres");
        }

        // Validar título (FCM requiere título o cuerpo)
        if ((push.getTitle() == null || push.getTitle().isBlank())
                && (push.getBody() == null || push.getBody().isBlank())) {
            errors.add("Se requiere título o cuerpo para notificación push");
        }

        // Validar TTL
        if (push.getTtlSeconds() != null && push.getTtlSeconds() < 0) {
            errors.add("El TTL debe ser un número positivo");
        }

        // Validar tamaño del payload de datos (límite FCM es 4KB)
        if (push.getData() != null) {
            int estimatedSize = push.getData().entrySet().stream()
                    .mapToInt(e -> e.getKey().length() + e.getValue().length())
                    .sum();
            if (estimatedSize > 4096) {
                errors.add("El payload de datos excede el tamaño máximo de 4KB");
            }
        }
    }

    @Override
    public boolean isConfigured() {
        // Firebase típicamente usa credenciales de cuenta de servicio
        return config != null
                && (config.getApiKey() != null || config.getProperty("serviceAccountPath") != null);
    }
}

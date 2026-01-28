package com.notifications.provider.sms;

import com.notifications.config.ProviderConfig;
import com.notifications.core.Notification;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.model.SmsNotification;
import com.notifications.provider.AbstractProvider;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementación del proveedor de SMS Vonage (anteriormente Nexmo).
 *
 * Esta es una implementación simulada que demuestra la estructura
 * y validación que sería requerida para una integración real con Vonage.
 *
 * La implementación real usaría el SDK de Java de Vonage:
 * - com.vonage:client
 * - POST https://rest.nexmo.com/sms/json
 *
 * @see <a href="https://developer.vonage.com/messaging/sms/overview">API de Vonage</a>
 */
public class VonageProvider extends AbstractProvider {

    private static final String NAME = "vonage";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    public VonageProvider(ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    protected NotificationResult doSend(Notification notification) {
        SmsNotification sms = (SmsNotification) notification;

        log.info("[SIMULADO] Vonage enviando SMS a: {}", sms.getPhoneNumber());
        log.debug("[SIMULADO] De/Sender ID: {}", sms.getSenderId() != null ? sms.getSenderId() : sms.getFrom());

        // Simular llamada a API
        // En implementación real:
        // VonageClient client = VonageClient.builder()
        //     .apiKey(apiKey)
        //     .apiSecret(apiSecret)
        //     .build();
        // SmsSubmissionResponse response = client.getSmsClient().submitMessage(
        //     new TextMessage(from, to, text)
        // );

        String messageId = generateMessageId();
        log.info("[SIMULADO] SMS de Vonage enviado exitosamente. MessageId: {}", messageId);

        return successResult(messageId);
    }

    @Override
    protected void validateSpecific(Notification notification, List<String> errors) {
        if (!(notification instanceof SmsNotification sms)) {
            errors.add("Se esperaba SmsNotification pero se recibió " + notification.getClass().getSimpleName());
            return;
        }

        // Validar número de teléfono
        if (!isValidPhoneNumber(sms.getPhoneNumber())) {
            errors.add("Formato de número de teléfono inválido: " + sms.getPhoneNumber());
        }

        // Vonage soporta IDs de remitente alfanuméricos
        if (sms.getSenderId() != null && sms.getSenderId().length() > 11) {
            errors.add("El Sender ID debe tener 11 caracteres o menos");
        }

        // Validar longitud del mensaje
        if (sms.getMessage() != null && sms.getMessage().length() > 1000) {
            errors.add("El mensaje excede la longitud máxima de 1000 caracteres");
        }
    }

    @Override
    public boolean isConfigured() {
        return config != null
                && config.getApiKey() != null
                && config.getApiSecret() != null;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}

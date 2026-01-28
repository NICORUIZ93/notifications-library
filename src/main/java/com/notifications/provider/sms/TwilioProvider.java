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
 * Implementación del proveedor de SMS Twilio.
 *
 * Esta es una implementación simulada que demuestra la estructura
 * y validación que sería requerida para una integración real con Twilio.
 *
 * La implementación real usaría el SDK de Java de Twilio:
 * - com.twilio.sdk:twilio
 * - POST /2010-04-01/Accounts/{AccountSid}/Messages.json
 *
 * @see <a href="https://www.twilio.com/docs/sms/api/message-resource">API de Twilio</a>
 */
public class TwilioProvider extends AbstractProvider {

    private static final String NAME = "twilio";
    // Formato E.164: +[código de país][número]
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");

    public TwilioProvider(ProviderConfig config) {
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

        log.info("[SIMULADO] Twilio enviando SMS a: {}", sms.getPhoneNumber());
        log.debug("[SIMULADO] De: {}", sms.getFrom());
        log.debug("[SIMULADO] Longitud del mensaje: {} caracteres", sms.getMessage().length());

        // Simular llamada a API
        // En implementación real:
        // Twilio.init(accountSid, authToken);
        // Message message = Message.creator(
        //     new PhoneNumber(to),
        //     new PhoneNumber(from),
        //     body
        // ).create();

        // Los SID de mensaje de Twilio empiezan con "SM"
        String messageId = "SM" + generateMessageId().replace("-", "").substring(0, 32);
        log.info("[SIMULADO] SMS de Twilio enviado exitosamente. SID: {}", messageId);

        return successResult(messageId);
    }

    @Override
    protected void validateSpecific(Notification notification, List<String> errors) {
        if (!(notification instanceof SmsNotification sms)) {
            errors.add("Se esperaba SmsNotification pero se recibió " + notification.getClass().getSimpleName());
            return;
        }

        // Validar formato de número de teléfono (E.164)
        if (!isValidPhoneNumber(sms.getPhoneNumber())) {
            errors.add("Formato de número de teléfono inválido. Se espera formato E.164 (ej: +5491155551234): " + sms.getPhoneNumber());
        }

        // Validar número de origen
        if (sms.getFrom() != null && !isValidPhoneNumber(sms.getFrom())) {
            errors.add("Formato de número 'from' inválido: " + sms.getFrom());
        }

        // Validar longitud del mensaje (límite de SMS es 1600 caracteres con Twilio)
        if (sms.getMessage() != null && sms.getMessage().length() > 1600) {
            errors.add("El mensaje excede la longitud máxima de 1600 caracteres");
        }
    }

    @Override
    public boolean isConfigured() {
        return config != null
                && config.getAccountId() != null
                && config.getAuthToken() != null;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}

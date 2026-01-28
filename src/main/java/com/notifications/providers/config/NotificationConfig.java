package com.notifications.providers.config;

import com.notifications.NotificationService;
import com.notifications.channels.email.SendGridProvider;
import com.notifications.channels.push.FirebaseProvider;
import com.notifications.channels.sms.TwilioProvider;

/**
 * Clase de configuración para la creación de instancias del servicio de notificaciones.
 * Proporciona métodos de fábrica para configuraciones predefinidas.
 */
public class NotificationConfig {

    /**
     * Crea un servicio con la configuración predeterminada.
     * Incluye SendGrid para email, Twilio para SMS y Firebase para push.
     *
     * @return Instancia configurada del servicio de notificaciones
     */
    public static NotificationService createDefaultService() {
        return new NotificationService.Builder()
                .withEmailChannel(new SendGridProvider("sendgrid-api-key"))
                .withSmsChannel(new TwilioProvider(
                        "twilio-account-sid",
                        "twilio-auth-token",
                        "+1234567890"))
                .withPushChannel(new FirebaseProvider("firebase-service-account-key"))
                .build();
    }

    /**
     * Crea un servicio con configuración personalizada.
     *
     * @param emailApiKey           API key de SendGrid
     * @param smsAccountSid         Account SID de Twilio
     * @param smsAuthToken          Auth Token de Twilio
     * @param smsFromNumber         Número de origen para SMS
     * @param pushServiceAccountKey Service Account Key de Firebase
     * @return Instancia configurada del servicio de notificaciones
     */
    public static NotificationService createCustomService(
            String emailApiKey,
            String smsAccountSid,
            String smsAuthToken,
            String smsFromNumber,
            String pushServiceAccountKey) {

        NotificationService.Builder builder = new NotificationService.Builder();

        builder.withEmailChannel(new SendGridProvider(emailApiKey));
        builder.withSmsChannel(new TwilioProvider(smsAccountSid, smsAuthToken, smsFromNumber));

        if (pushServiceAccountKey != null) {
            builder.withPushChannel(new FirebaseProvider(pushServiceAccountKey));
        }

        return builder.build();
    }
}

package com.notifications;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.model.EmailNotification;
import com.notifications.model.PushNotification;
import com.notifications.model.SmsNotification;
import com.notifications.provider.email.SendGridProvider;
import com.notifications.provider.push.FirebaseProvider;
import com.notifications.provider.sms.TwilioProvider;

public class Main {

    public static void main(String[] args) {
        NotificationConfig config = NotificationConfig.builder()
                .emailProvider("sendgrid", ProviderConfig.builder()
                        .apiKey("SG.api-key")
                        .build())
                .smsProvider("twilio", ProviderConfig.builder()
                        .accountId("AC_account_sid")
                        .authToken("auth_token")
                        .build())
                .pushProvider("firebase", ProviderConfig.builder()
                        .apiKey("firebase-key")
                        .property("projectId", "mi-proyecto")
                        .build())
                .build();

        NotificationService service = NotificationService.builder()
                .config(config)
                .register(new SendGridProvider(config.getProviderConfig(NotificationChannel.EMAIL, "sendgrid")))
                .register(new TwilioProvider(config.getProviderConfig(NotificationChannel.SMS, "twilio")))
                .register(new FirebaseProvider(config.getProviderConfig(NotificationChannel.PUSH, "firebase")))
                .build();

        // Email
        EmailNotification email = EmailNotification.builder()
                .recipient("usuario@ejemplo.com")
                .from("noreply@app.com")
                .subject("Bienvenido")
                .message("Gracias por registrarte")
                .build();

        NotificationResult resultEmail = service.send(email);
        System.out.println("Email: " + (resultEmail.isSuccess() ? "OK" : "FALLO"));

        // SMS
        SmsNotification sms = SmsNotification.builder()
                .recipient("+5491155551234")
                .message("Tu codigo es: 123456")
                .build();

        NotificationResult resultSms = service.send(sms);
        System.out.println("SMS: " + (resultSms.isSuccess() ? "OK" : "FALLO"));

        // Push
        PushNotification push = PushNotification.builder()
                .recipient("a".repeat(152))
                .title("Nueva notificacion")
                .message("Tienes un mensaje nuevo")
                .build();

        NotificationResult resultPush = service.send(push);
        System.out.println("Push: " + (resultPush.isSuccess() ? "OK" : "FALLO"));

        service.shutdown();
    }
}

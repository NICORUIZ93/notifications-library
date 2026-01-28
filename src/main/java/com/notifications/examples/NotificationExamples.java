package com.notifications.examples;

import com.notifications.NotificationService;
import com.notifications.channels.email.SendGridProvider;
import com.notifications.channels.push.FirebaseProvider;
import com.notifications.channels.sms.TwilioProvider;
import com.notifications.core.ChannelType;
import com.notifications.core.Notification;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;

import java.util.Arrays;
import java.util.List;

/**
 * Ejemplos de uso de la librería de notificaciones.
 */
public class NotificationExamples {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   Ejemplos de Notifications Library");
        System.out.println("===========================================\n");

        ejemploEnvioEmail();
        ejemploEnvioSms();
        ejemploEnvioPush();
        ejemploEnvioMultiple();

        System.out.println("\n===========================================");
        System.out.println("   Ejemplos completados exitosamente");
        System.out.println("===========================================");
    }

    /**
     * Ejemplo de envío de notificación por email.
     */
    private static void ejemploEnvioEmail() {
        System.out.println("1. Envío de Email");
        System.out.println("-----------------");

        NotificationService service = new NotificationService.Builder()
                .withEmailChannel(new SendGridProvider("demo-api-key"))
                .build();

        Notification notification = Notification.builder()
                .id("email-001")
                .recipient("usuario@ejemplo.com")
                .subject("Bienvenido a nuestra plataforma")
                .content("Gracias por registrarte. Tu cuenta ha sido creada exitosamente.")
                .build();

        try {
            NotificationResult result = service.send(notification);
            System.out.println("   Resultado: " + (result.isSuccess() ? "Exitoso" : "Fallido"));
            System.out.println("   ID del proveedor: " + result.getProviderMessageId());
        } catch (NotificationException e) {
            System.out.println("   Error: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * Ejemplo de envío de notificación por SMS.
     */
    private static void ejemploEnvioSms() {
        System.out.println("2. Envío de SMS");
        System.out.println("---------------");

        NotificationService service = new NotificationService.Builder()
                .withSmsChannel(new TwilioProvider("demo-sid", "demo-token", "+1234567890"))
                .build();

        Notification notification = Notification.builder()
                .id("sms-001")
                .recipient("+5491123456789")
                .content("Tu código de verificación es: 123456")
                .build();

        try {
            NotificationResult result = service.send(notification);
            System.out.println("   Resultado: " + (result.isSuccess() ? "Exitoso" : "Fallido"));
            System.out.println("   ID del proveedor: " + result.getProviderMessageId());
        } catch (NotificationException e) {
            System.out.println("   Error: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * Ejemplo de envío de notificación push.
     */
    private static void ejemploEnvioPush() {
        System.out.println("3. Envío de Push Notification");
        System.out.println("-----------------------------");

        NotificationService service = new NotificationService.Builder()
                .withPushChannel(new FirebaseProvider("demo-firebase-key"))
                .build();

        Notification notification = Notification.builder()
                .id("push-001")
                .recipient("device-token-abc123")
                .subject("Nueva promoción")
                .content("Aprovecha 50% de descuento en todos los productos")
                .build();

        try {
            NotificationResult result = service.send(notification);
            System.out.println("   Resultado: " + (result.isSuccess() ? "Exitoso" : "Fallido"));
            System.out.println("   ID del proveedor: " + result.getProviderMessageId());
        } catch (NotificationException e) {
            System.out.println("   Error: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * Ejemplo de envío múltiple (batch).
     */
    private static void ejemploEnvioMultiple() {
        System.out.println("4. Envío Múltiple (Batch)");
        System.out.println("-------------------------");

        NotificationService service = new NotificationService.Builder()
                .withEmailChannel(new SendGridProvider("demo-api-key"))
                .withSmsChannel(new TwilioProvider("demo-sid", "demo-token", "+1234567890"))
                .withPushChannel(new FirebaseProvider("demo-firebase-key"))
                .build();

        List<Notification> notifications = Arrays.asList(
                Notification.builder()
                        .id("batch-001")
                        .recipient("usuario1@ejemplo.com")
                        .subject("Notificación 1")
                        .content("Contenido del mensaje 1")
                        .preferredChannel(ChannelType.EMAIL)
                        .build(),
                Notification.builder()
                        .id("batch-002")
                        .recipient("+5491198765432")
                        .content("Mensaje SMS de prueba")
                        .preferredChannel(ChannelType.SMS)
                        .build(),
                Notification.builder()
                        .id("batch-003")
                        .recipient("device-token-xyz789")
                        .subject("Push de prueba")
                        .content("Contenido del push")
                        .preferredChannel(ChannelType.PUSH)
                        .build()
        );

        List<NotificationResult> results = service.sendBatch(notifications);

        System.out.println("   Notificaciones enviadas: " + results.size());
        for (NotificationResult result : results) {
            System.out.println("   - " + result.getNotificationId() +
                    " (" + result.getChannelType() + "): " +
                    (result.isSuccess() ? "Exitoso" : "Fallido"));
        }
    }
}

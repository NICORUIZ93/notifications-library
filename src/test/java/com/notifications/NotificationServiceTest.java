package com.notifications;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.exception.ConfigurationException;
import com.notifications.exception.ValidationException;
import com.notifications.model.EmailNotification;
import com.notifications.model.PushNotification;
import com.notifications.model.SmsNotification;
import com.notifications.provider.email.SendGridProvider;
import com.notifications.provider.push.FirebaseProvider;
import com.notifications.provider.sms.TwilioProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests de NotificationService")
class NotificationServiceTest {

    private NotificationService service;
    private NotificationConfig config;

    @BeforeEach
    void setUp() {
        config = NotificationConfig.builder()
                .emailProvider("sendgrid", ProviderConfig.builder()
                        .apiKey("test-api-key")
                        .build())
                .smsProvider("twilio", ProviderConfig.builder()
                        .accountId("test-account")
                        .authToken("test-token")
                        .build())
                .pushProvider("firebase", ProviderConfig.builder()
                        .apiKey("test-firebase-key")
                        .property("projectId", "test-project")
                        .build())
                .build();

        service = NotificationService.builder()
                .config(config)
                .register(new SendGridProvider(config.getProviderConfig(NotificationChannel.EMAIL, "sendgrid")))
                .register(new TwilioProvider(config.getProviderConfig(NotificationChannel.SMS, "twilio")))
                .register(new FirebaseProvider(config.getProviderConfig(NotificationChannel.PUSH, "firebase")))
                .build();
    }

    @Nested
    @DisplayName("Notificaciones de Email")
    class NotificacionesEmail {

        @Test
        @DisplayName("Debería enviar email exitosamente")
        void deberiaEnviarEmailExitosamente() {
            EmailNotification email = EmailNotification.builder()
                    .recipient("usuario@ejemplo.com")
                    .from("remitente@ejemplo.com")
                    .subject("Asunto de Prueba")
                    .message("Cuerpo del mensaje de prueba")
                    .build();

            NotificationResult result = service.send(email);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getChannel()).isEqualTo(NotificationChannel.EMAIL);
            assertThat(result.getProviderName()).isEqualTo("sendgrid");
            assertThat(result.getMessageId()).isPresent();
        }

        @Test
        @DisplayName("Debería fallar con formato de email inválido")
        void deberiaFallarConFormatoEmailInvalido() {
            EmailNotification email = EmailNotification.builder()
                    .recipient("email-invalido")
                    .subject("Prueba")
                    .message("Prueba")
                    .build();

            assertThatThrownBy(() -> service.send(email))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Validación fallida");
        }

        @Test
        @DisplayName("Debería fallar sin asunto")
        void deberiaFallarSinAsunto() {
            EmailNotification email = EmailNotification.builder()
                    .recipient("usuario@ejemplo.com")
                    .message("Prueba")
                    .build();

            assertThatThrownBy(() -> service.send(email))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Nested
    @DisplayName("Notificaciones SMS")
    class NotificacionesSms {

        @Test
        @DisplayName("Debería enviar SMS exitosamente")
        void deberiaEnviarSmsExitosamente() {
            SmsNotification sms = SmsNotification.builder()
                    .recipient("+5491155551234")
                    .from("+5491155559999")
                    .message("Mensaje SMS de prueba")
                    .build();

            NotificationResult result = service.send(sms);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getChannel()).isEqualTo(NotificationChannel.SMS);
            assertThat(result.getProviderName()).isEqualTo("twilio");
            assertThat(result.getMessageId()).isPresent();
            assertThat(result.getMessageId().get()).startsWith("SM");
        }

        @Test
        @DisplayName("Debería fallar con número de teléfono inválido")
        void deberiaFallarConTelefonoInvalido() {
            SmsNotification sms = SmsNotification.builder()
                    .recipient("12345")
                    .message("Prueba")
                    .build();

            assertThatThrownBy(() -> service.send(sms))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Validación fallida");
        }
    }

    @Nested
    @DisplayName("Notificaciones Push")
    class NotificacionesPush {

        @Test
        @DisplayName("Debería enviar notificación push exitosamente")
        void deberiaEnviarPushExitosamente() {
            String deviceToken = "a".repeat(152);

            PushNotification push = PushNotification.builder()
                    .recipient(deviceToken)
                    .title("Título de Prueba")
                    .message("Cuerpo push de prueba")
                    .build();

            NotificationResult result = service.send(push);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getChannel()).isEqualTo(NotificationChannel.PUSH);
            assertThat(result.getProviderName()).isEqualTo("firebase");
        }

        @Test
        @DisplayName("Debería fallar con token de dispositivo inválido")
        void deberiaFallarConTokenInvalido() {
            PushNotification push = PushNotification.builder()
                    .recipient("token-corto")
                    .title("Prueba")
                    .message("Prueba")
                    .build();

            assertThatThrownBy(() -> service.send(push))
                    .isInstanceOf(ValidationException.class);
        }
    }

    @Nested
    @DisplayName("Operaciones Asíncronas")
    class OperacionesAsincronas {

        @Test
        @DisplayName("Debería enviar email asincrónicamente")
        void deberiaEnviarEmailAsync() throws ExecutionException, InterruptedException {
            EmailNotification email = EmailNotification.builder()
                    .recipient("usuario@ejemplo.com")
                    .from("remitente@ejemplo.com")
                    .subject("Prueba Async")
                    .message("Mensaje asíncrono de prueba")
                    .build();

            CompletableFuture<NotificationResult> future = service.sendAsync(email);
            NotificationResult result = future.get();

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("Debería enviar notificaciones en lote")
        void deberiaEnviarEnLote() throws ExecutionException, InterruptedException {
            EmailNotification email = EmailNotification.builder()
                    .recipient("usuario@ejemplo.com")
                    .from("remitente@ejemplo.com")
                    .subject("Prueba Lote")
                    .message("Mensaje de email")
                    .build();

            SmsNotification sms = SmsNotification.builder()
                    .recipient("+5491155551234")
                    .message("Mensaje SMS")
                    .build();

            CompletableFuture<List<NotificationResult>> future = service.sendBatch(List.of(email, sms));
            List<NotificationResult> results = future.get();

            assertThat(results).hasSize(2);
            assertThat(results).allMatch(NotificationResult::isSuccess);
        }
    }

    @Nested
    @DisplayName("Selección de Proveedor")
    class SeleccionProveedor {

        @Test
        @DisplayName("Debería lanzar excepción para canal no configurado")
        void deberiaLanzarExcepcionParaCanalNoConfigurado() {
            NotificationService servicioVacio = NotificationService.builder().build();

            EmailNotification email = EmailNotification.builder()
                    .recipient("usuario@ejemplo.com")
                    .subject("Prueba")
                    .message("Prueba")
                    .build();

            assertThatThrownBy(() -> servicioVacio.send(email))
                    .isInstanceOf(ConfigurationException.class)
                    .hasMessageContaining("No hay proveedor configurado");
        }

        @Test
        @DisplayName("Debería lanzar excepción para proveedor desconocido")
        void deberiaLanzarExcepcionParaProveedorDesconocido() {
            EmailNotification email = EmailNotification.builder()
                    .recipient("usuario@ejemplo.com")
                    .subject("Prueba")
                    .message("Prueba")
                    .build();

            assertThatThrownBy(() -> service.send(email, "proveedor-desconocido"))
                    .isInstanceOf(ConfigurationException.class)
                    .hasMessageContaining("no encontrado");
        }
    }
}

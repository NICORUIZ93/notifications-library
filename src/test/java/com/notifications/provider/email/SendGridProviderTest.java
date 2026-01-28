package com.notifications.provider.email;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.exception.ValidationException;
import com.notifications.model.EmailNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests de SendGridProvider")
class SendGridProviderTest {

    private SendGridProvider provider;

    @BeforeEach
    void setUp() {
        ProviderConfig config = ProviderConfig.builder()
                .apiKey("test-api-key")
                .build();
        provider = new SendGridProvider(config);
    }

    @Test
    @DisplayName("Debería retornar nombre correcto del proveedor")
    void deberiaRetornarNombreCorrecto() {
        assertThat(provider.getName()).isEqualTo("sendgrid");
    }

    @Test
    @DisplayName("Debería retornar canal EMAIL")
    void deberiaRetornarCanalEmail() {
        assertThat(provider.getChannel()).isEqualTo(NotificationChannel.EMAIL);
    }

    @Test
    @DisplayName("Debería enviar email exitosamente")
    void deberiaEnviarEmailExitosamente() {
        EmailNotification email = EmailNotification.builder()
                .recipient("test@ejemplo.com")
                .from("remitente@ejemplo.com")
                .subject("Asunto de Prueba")
                .message("Mensaje de prueba")
                .build();

        NotificationResult result = provider.send(email);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessageId()).isPresent();
        assertThat(result.getProviderName()).isEqualTo("sendgrid");
    }

    @Test
    @DisplayName("Debería enviar email con CC y BCC")
    void deberiaEnviarEmailConCcYBcc() {
        EmailNotification email = EmailNotification.builder()
                .recipient("test@ejemplo.com")
                .from("remitente@ejemplo.com")
                .subject("Asunto de Prueba")
                .message("Mensaje de prueba")
                .cc(List.of("cc1@ejemplo.com", "cc2@ejemplo.com"))
                .bcc(List.of("bcc@ejemplo.com"))
                .build();

        NotificationResult result = provider.send(email);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Debería enviar email con contenido HTML")
    void deberiaEnviarEmailConHtml() {
        EmailNotification email = EmailNotification.builder()
                .recipient("test@ejemplo.com")
                .from("remitente@ejemplo.com")
                .subject("Email HTML")
                .message("Respaldo en texto plano")
                .htmlContent("<h1>Hola</h1><p>Este es contenido HTML</p>")
                .build();

        NotificationResult result = provider.send(email);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Debería fallar validación sin destinatario")
    void deberiaFallarSinDestinatario() {
        EmailNotification email = EmailNotification.builder()
                .from("remitente@ejemplo.com")
                .subject("Prueba")
                .message("Prueba")
                .build();

        assertThatThrownBy(() -> provider.send(email))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Debería fallar validación con formato de email inválido")
    void deberiaFallarConFormatoEmailInvalido() {
        EmailNotification email = EmailNotification.builder()
                .recipient("no-es-un-email")
                .subject("Prueba")
                .message("Prueba")
                .build();

        assertThatThrownBy(() -> provider.send(email))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("inválido"));
                });
    }

    @Test
    @DisplayName("Debería fallar validación sin asunto")
    void deberiaFallarSinAsunto() {
        EmailNotification email = EmailNotification.builder()
                .recipient("test@ejemplo.com")
                .message("Prueba")
                .build();

        assertThatThrownBy(() -> provider.send(email))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("asunto"));
                });
    }

    @Test
    @DisplayName("Debería estar configurado con API key")
    void deberiaEstarConfiguradoConApiKey() {
        assertThat(provider.isConfigured()).isTrue();
    }

    @Test
    @DisplayName("No debería estar configurado sin API key")
    void noDeberiaEstarConfiguradoSinApiKey() {
        SendGridProvider sinConfigurar = new SendGridProvider(ProviderConfig.builder().build());
        assertThat(sinConfigurar.isConfigured()).isFalse();
    }
}

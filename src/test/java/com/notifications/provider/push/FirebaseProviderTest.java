package com.notifications.provider.push;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.exception.ValidationException;
import com.notifications.model.PushNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests de FirebaseProvider")
class FirebaseProviderTest {

    private FirebaseProvider provider;
    private String tokenValido;

    @BeforeEach
    void setUp() {
        ProviderConfig config = ProviderConfig.builder()
                .apiKey("test-firebase-key")
                .property("projectId", "test-project")
                .build();
        provider = new FirebaseProvider(config);

        // Los tokens FCM típicamente tienen 140-200 caracteres
        tokenValido = "a".repeat(152);
    }

    @Test
    @DisplayName("Debería retornar nombre correcto del proveedor")
    void deberiaRetornarNombreCorrecto() {
        assertThat(provider.getName()).isEqualTo("firebase");
    }

    @Test
    @DisplayName("Debería retornar canal PUSH")
    void deberiaRetornarCanalPush() {
        assertThat(provider.getChannel()).isEqualTo(NotificationChannel.PUSH);
    }

    @Test
    @DisplayName("Debería enviar notificación push exitosamente")
    void deberiaEnviarPushExitosamente() {
        PushNotification push = PushNotification.builder()
                .recipient(tokenValido)
                .title("Título de Prueba")
                .message("Mensaje cuerpo de prueba")
                .build();

        NotificationResult result = provider.send(push);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessageId()).isPresent();
        assertThat(result.getMessageId().get()).contains("projects/test-project/messages/");
        assertThat(result.getProviderName()).isEqualTo("firebase");
    }

    @Test
    @DisplayName("Debería enviar push con todos los campos opcionales")
    void deberiaEnviarPushConTodosLosCampos() {
        PushNotification push = PushNotification.builder()
                .recipient(tokenValido)
                .title("Prueba Completa")
                .message("Cuerpo")
                .imageUrl("https://ejemplo.com/imagen.png")
                .badge(5)
                .sound("default")
                .data(Map.of("clave1", "valor1", "clave2", "valor2"))
                .topic("noticias")
                .priority(PushNotification.Priority.HIGH)
                .ttlSeconds(3600)
                .build();

        NotificationResult result = provider.send(push);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Debería fallar con token de dispositivo muy corto")
    void deberiaFallarConTokenCorto() {
        PushNotification push = PushNotification.builder()
                .recipient("token-corto")
                .title("Prueba")
                .message("Prueba")
                .build();

        assertThatThrownBy(() -> provider.send(push))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("token"));
                });
    }

    @Test
    @DisplayName("Debería fallar sin título ni cuerpo")
    void deberiaFallarSinTituloNiCuerpo() {
        PushNotification push = PushNotification.builder()
                .recipient(tokenValido)
                .build();

        assertThatThrownBy(() -> provider.send(push))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("título o cuerpo"));
                });
    }

    @Test
    @DisplayName("Debería tener éxito solo con título")
    void deberiaExitarSoloConTitulo() {
        PushNotification push = PushNotification.builder()
                .recipient(tokenValido)
                .title("Solo Título")
                .build();

        NotificationResult result = provider.send(push);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Debería tener éxito solo con cuerpo")
    void deberiaExitarSoloConCuerpo() {
        PushNotification push = PushNotification.builder()
                .recipient(tokenValido)
                .message("Solo Cuerpo")
                .build();

        NotificationResult result = provider.send(push);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Debería fallar con TTL negativo")
    void deberiaFallarConTtlNegativo() {
        PushNotification push = PushNotification.builder()
                .recipient(tokenValido)
                .title("Prueba")
                .ttlSeconds(-1)
                .build();

        assertThatThrownBy(() -> provider.send(push))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("TTL"));
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
        FirebaseProvider sinConfigurar = new FirebaseProvider(ProviderConfig.builder().build());
        assertThat(sinConfigurar.isConfigured()).isFalse();
    }
}

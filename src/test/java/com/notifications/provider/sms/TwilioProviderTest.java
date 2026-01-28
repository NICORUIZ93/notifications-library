package com.notifications.provider.sms;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.exception.ValidationException;
import com.notifications.model.SmsNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests de TwilioProvider")
class TwilioProviderTest {

    private TwilioProvider provider;

    @BeforeEach
    void setUp() {
        ProviderConfig config = ProviderConfig.builder()
                .accountId("AC_test_account")
                .authToken("test_auth_token")
                .build();
        provider = new TwilioProvider(config);
    }

    @Test
    @DisplayName("Debería retornar nombre correcto del proveedor")
    void deberiaRetornarNombreCorrecto() {
        assertThat(provider.getName()).isEqualTo("twilio");
    }

    @Test
    @DisplayName("Debería retornar canal SMS")
    void deberiaRetornarCanalSms() {
        assertThat(provider.getChannel()).isEqualTo(NotificationChannel.SMS);
    }

    @Test
    @DisplayName("Debería enviar SMS exitosamente")
    void deberiaEnviarSmsExitosamente() {
        SmsNotification sms = SmsNotification.builder()
                .recipient("+5491155551234")
                .from("+5491155559999")
                .message("Mensaje SMS de prueba")
                .build();

        NotificationResult result = provider.send(sms);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessageId()).isPresent();
        assertThat(result.getMessageId().get()).startsWith("SM");
        assertThat(result.getProviderName()).isEqualTo("twilio");
    }

    @ParameterizedTest
    @ValueSource(strings = {"+5491155551234", "+442071838750", "+14155551234"})
    @DisplayName("Debería aceptar números de teléfono E.164 válidos")
    void deberiaAceptarNumerosE164Validos(String numeroTelefono) {
        SmsNotification sms = SmsNotification.builder()
                .recipient(numeroTelefono)
                .message("Prueba")
                .build();

        NotificationResult result = provider.send(sms);

        assertThat(result.isSuccess()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "invalido", "+1234", "5491155551234", "+0123456789"})
    @DisplayName("Debería rechazar números de teléfono inválidos")
    void deberiaRechazarNumerosInvalidos(String numeroTelefono) {
        SmsNotification sms = SmsNotification.builder()
                .recipient(numeroTelefono)
                .message("Prueba")
                .build();

        assertThatThrownBy(() -> provider.send(sms))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("teléfono"));
                });
    }

    @Test
    @DisplayName("Debería fallar sin destinatario")
    void deberiaFallarSinDestinatario() {
        SmsNotification sms = SmsNotification.builder()
                .message("Prueba")
                .build();

        assertThatThrownBy(() -> provider.send(sms))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Debería fallar con mensaje excediendo longitud máxima")
    void deberiaFallarConMensajeMuyLargo() {
        SmsNotification sms = SmsNotification.builder()
                .recipient("+5491155551234")
                .message("x".repeat(1601))
                .build();

        assertThatThrownBy(() -> provider.send(sms))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> {
                    ValidationException ve = (ValidationException) e;
                    assertThat(ve.getValidationErrors()).anyMatch(err -> err.contains("longitud máxima"));
                });
    }

    @Test
    @DisplayName("Debería estar configurado con account ID y auth token")
    void deberiaEstarConfiguradoConCredenciales() {
        assertThat(provider.isConfigured()).isTrue();
    }

    @Test
    @DisplayName("No debería estar configurado sin credenciales")
    void noDeberiaEstarConfiguradoSinCredenciales() {
        TwilioProvider sinConfigurar = new TwilioProvider(ProviderConfig.builder().build());
        assertThat(sinConfigurar.isConfigured()).isFalse();
    }
}

package com.notifications.provider.email;

import com.notifications.config.ProviderConfig;
import com.notifications.core.Notification;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationResult;
import com.notifications.model.EmailNotification;
import com.notifications.provider.AbstractProvider;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementación del proveedor de email Mailgun.
 *
 * Esta es una implementación simulada que demuestra la estructura
 * y validación que sería requerida para una integración real con Mailgun.
 *
 * La implementación real usaría la API REST de Mailgun:
 * - POST https://api.mailgun.net/v3/{domain}/messages
 *
 * @see <a href="https://documentation.mailgun.com/en/latest/api-sending.html">API de Mailgun</a>
 */
public class MailgunProvider extends AbstractProvider {

    private static final String NAME = "mailgun";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public MailgunProvider(ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    protected NotificationResult doSend(Notification notification) {
        EmailNotification email = (EmailNotification) notification;

        log.info("[SIMULADO] Mailgun enviando email a: {}", email.getRecipient());
        log.debug("[SIMULADO] Asunto: {}", email.getSubject());
        log.debug("[SIMULADO] Dominio: {}", config.getProperty("domain"));

        // Simular llamada a API
        // En implementación real:
        // POST https://api.mailgun.net/v3/{domain}/messages
        // con form data: from, to, subject, text, html

        String messageId = "<" + generateMessageId() + "@" + config.getProperty("domain") + ">";
        log.info("[SIMULADO] Email de Mailgun enviado exitosamente. MessageId: {}", messageId);

        return successResult(messageId);
    }

    @Override
    protected void validateSpecific(Notification notification, List<String> errors) {
        if (!(notification instanceof EmailNotification email)) {
            errors.add("Se esperaba EmailNotification pero se recibió " + notification.getClass().getSimpleName());
            return;
        }

        // Validar formato de email
        if (!isValidEmail(email.getRecipient())) {
            errors.add("Formato de email de destinatario inválido: " + email.getRecipient());
        }

        // Validar asunto
        if (email.getSubject() == null || email.getSubject().isBlank()) {
            errors.add("El asunto es requerido para email");
        }
    }

    @Override
    public boolean isConfigured() {
        return config != null
                && config.getApiKey() != null
                && config.getProperty("domain") != null;
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}

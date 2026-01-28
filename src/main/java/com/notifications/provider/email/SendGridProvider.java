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
 * Implementación del proveedor de email SendGrid.
 *
 * Esta es una implementación simulada que demuestra la estructura
 * y validación que sería requerida para una integración real con SendGrid.
 *
 * La implementación real usaría el SDK de Java de SendGrid:
 * - com.sendgrid:sendgrid-java
 * - POST /v3/mail/send
 *
 * @see <a href="https://docs.sendgrid.com/api-reference/mail-send/mail-send">API de SendGrid</a>
 */
public class SendGridProvider extends AbstractProvider {

    private static final String NAME = "sendgrid";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public SendGridProvider(ProviderConfig config) {
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

        log.info("[SIMULADO] SendGrid enviando email a: {}", email.getRecipient());
        log.debug("[SIMULADO] Asunto: {}", email.getSubject());
        log.debug("[SIMULADO] De: {}", email.getFrom());

        // Simular llamada a API
        // En implementación real:
        // Mail mail = new Mail(from, subject, to, content);
        // SendGrid sg = new SendGrid(config.getApiKey());
        // Request request = new Request();
        // request.setMethod(Method.POST);
        // request.setEndpoint("mail/send");
        // request.setBody(mail.build());
        // Response response = sg.api(request);

        String messageId = generateMessageId();
        log.info("[SIMULADO] Email de SendGrid enviado exitosamente. MessageId: {}", messageId);

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

        // Validar dirección de remitente
        if (email.getFrom() != null && !isValidEmail(email.getFrom())) {
            errors.add("Formato de email de remitente inválido: " + email.getFrom());
        }

        // Validar asunto (SendGrid requiere asunto)
        if (email.getSubject() == null || email.getSubject().isBlank()) {
            errors.add("El asunto es requerido para email");
        }

        // Validar direcciones CC
        if (email.getCc() != null) {
            for (String cc : email.getCc()) {
                if (!isValidEmail(cc)) {
                    errors.add("Formato de email CC inválido: " + cc);
                }
            }
        }

        // Validar direcciones BCC
        if (email.getBcc() != null) {
            for (String bcc : email.getBcc()) {
                if (!isValidEmail(bcc)) {
                    errors.add("Formato de email BCC inválido: " + bcc);
                }
            }
        }
    }

    @Override
    public boolean isConfigured() {
        return config != null && config.getApiKey() != null && !config.getApiKey().isBlank();
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}

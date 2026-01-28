package com.notifications.channels.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * Implementación simulada del proveedor SendGrid para envío de correos electrónicos.
 * En producción, esta clase realizaría llamadas HTTP a la API de SendGrid.
 */
@Slf4j
@RequiredArgsConstructor
public class SendGridProvider implements EmailProvider {

    private final String apiKey;

    @Override
    public String sendEmail(Set<String> recipients, String subject, String content, Map<String, Object> metadata) {
        log.info("[SENDGRID] Enviando email a {} destinatarios", recipients.size());
        log.info("[SENDGRID] Asunto: {}", subject);
        log.debug("[SENDGRID] API Key: {}...", apiKey.substring(0, Math.min(apiKey.length(), 8)));

        String messageId = "sg_" + System.currentTimeMillis();
        log.info("[SENDGRID] Email enviado. ID: {}", messageId);

        return messageId;
    }
}

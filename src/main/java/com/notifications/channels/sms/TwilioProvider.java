package com.notifications.channels.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * Implementación simulada del proveedor Twilio para envío de SMS.
 * En producción, esta clase realizaría llamadas HTTP a la API de Twilio.
 */
@Slf4j
@RequiredArgsConstructor
public class TwilioProvider implements SmsProvider {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;

    @Override
    public String sendSms(Set<String> recipients, String message, Map<String, Object> metadata) {
        log.info("[TWILIO] Enviando SMS desde {} a {} destinatarios", fromNumber, recipients.size());
        log.info("[TWILIO] Mensaje: {}", message.length() > 50 ? message.substring(0, 50) + "..." : message);
        log.debug("[TWILIO] Account SID: {}...", accountSid.substring(0, Math.min(accountSid.length(), 8)));

        String messageId = "tw_" + System.currentTimeMillis();
        log.info("[TWILIO] SMS enviado. SID: {}", messageId);

        return messageId;
    }
}

package com.notifications.model;

import com.notifications.core.NotificationChannel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Notificación específica de email con soporte para asunto, contenido HTML,
 * CC, BCC y adjuntos.
 *
 * Decisión de Diseño: Email tiene campos únicos (asunto, HTML, CC/BCC) que
 * no aplican a otros canales. En lugar de poner campos opcionales en
 * la clase base, creamos un modelo específico para email.
 */
@Getter
public class EmailNotification extends BaseNotification {

    private final String from;
    private final String subject;
    private final String htmlContent;
    private final List<String> cc;
    private final List<String> bcc;
    private final List<Attachment> attachments;
    private final String replyTo;

    @Builder
    public EmailNotification(String recipient, String message, Map<String, Object> metadata,
                              String from, String subject, String htmlContent,
                              List<String> cc, List<String> bcc,
                              List<Attachment> attachments, String replyTo) {
        super(recipient, message, metadata);
        this.from = from;
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.cc = cc;
        this.bcc = bcc;
        this.attachments = attachments;
        this.replyTo = replyTo;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    /**
     * @return El contenido de texto (texto plano de respaldo)
     */
    public String getTextContent() {
        return getMessage();
    }

    /**
     * @return true si este email tiene contenido HTML
     */
    public boolean hasHtmlContent() {
        return htmlContent != null && !htmlContent.isBlank();
    }

    /**
     * @return true si este email tiene adjuntos
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * Representa un adjunto de email
     */
    @Getter
    @Builder
    public static class Attachment {
        private final String filename;
        private final String contentType;
        private final byte[] content;
        private final String contentId; // Para adjuntos inline
    }
}

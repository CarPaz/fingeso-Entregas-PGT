package com.fingesoHito3Grupo7.entregas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*
 * Servicio responsable de construir y enviar notificaciones simples por SMTP.
 *
 * En desarrollo local puede utilizar Mailpit en localhost:1025 sin usuario,
 * contraseña, autenticación ni TLS. En otros ambientes esos valores se
 * configuran mediante variables de entorno, sin modificar el código.
 */
@Service
public class EmailService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

    private static final String REMITENTE_LOCAL =
            "notificaciones@sistema.local";

    private final JavaMailSender mailSender;
    private final String remitentePorDefecto;

    /*
     * MAIL_FROM identifica al remitente visible del mensaje y es independiente
     * de MAIL_USER, ya que Mailpit local no necesita autenticación.
     */
    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:notificaciones@sistema.local}")
            String remitentePorDefecto
    ) {
        this.mailSender = mailSender;
        this.remitentePorDefecto =
                StringUtils.hasText(remitentePorDefecto)
                        ? remitentePorDefecto.trim()
                        : REMITENTE_LOCAL;
    }

    /*
     * Intenta enviar un correo y devuelve true cuando el servidor SMTP lo
     * acepta. Si el servidor no está disponible, registra el error y devuelve
     * false para que una notificación fallida no invalide la entrega guardada.
     */
    public boolean enviarCorreoSimple(
            String destinatario,
            String asunto,
            String mensaje
    ) {
        if (!StringUtils.hasText(destinatario)) {
            logger.warn(
                    "No se envió la notificación porque el destinatario está vacío."
            );
            return false;
        }

        if (!StringUtils.hasText(asunto) || !StringUtils.hasText(mensaje)) {
            logger.warn(
                    "No se envió la notificación a {} porque el asunto o el mensaje están vacíos.",
                    destinatario
            );
            return false;
        }

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(remitentePorDefecto);
            email.setTo(destinatario.trim());
            email.setSubject(asunto);
            email.setText(mensaje);

            mailSender.send(email);

            logger.info(
                    "Correo enviado exitosamente a {}.",
                    destinatario
            );
            return true;
        } catch (MailException | IllegalArgumentException exception) {
            logger.error(
                    "No fue posible enviar el correo a {}.",
                    destinatario,
                    exception
            );
            return false;
        }
    }
}

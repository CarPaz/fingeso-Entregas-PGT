package com.fingesoHito3Grupo7.entregas.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void debeEnviarCorreoConRemitenteConfigurado() {
        EmailService emailService = new EmailService(
                mailSender,
                "notificaciones@sistema.local"
        );

        boolean enviado = emailService.enviarCorreoSimple(
                "profesor@universidad.cl",
                "Nueva entrega",
                "Se registró una entrega."
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage email = captor.getValue();
        assertTrue(enviado);
        assertEquals(
                "notificaciones@sistema.local",
                email.getFrom()
        );
        assertEquals(
                "profesor@universidad.cl",
                email.getTo()[0]
        );
        assertEquals("Nueva entrega", email.getSubject());
    }

    @Test
    void debeInformarFalloSinPropagarErrorSmtp() {
        EmailService emailService = new EmailService(
                mailSender,
                "notificaciones@sistema.local"
        );

        doThrow(new MailSendException("SMTP no disponible"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        boolean enviado = emailService.enviarCorreoSimple(
                "profesor@universidad.cl",
                "Nueva entrega",
                "Se registró una entrega."
        );

        assertFalse(enviado);
    }

    @Test
    void noDebeIntentarEnvioSinDestinatario() {
        EmailService emailService = new EmailService(
                mailSender,
                "notificaciones@sistema.local"
        );

        boolean enviado = emailService.enviarCorreoSimple(
                " ",
                "Nueva entrega",
                "Se registró una entrega."
        );

        assertFalse(enviado);
        verify(mailSender, never())
                .send(any(SimpleMailMessage.class));
    }
}

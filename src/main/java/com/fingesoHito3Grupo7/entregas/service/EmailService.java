package com.fingesoHito3Grupo7.entregas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Al estar en local y como no hay usuario configurado se usara el correo de prueba configurado localmente
    @Value("${spring.mail.username:notificaciones@sistema.local}")
    private String remitentePorDefecto;


    //conexion del mortor de correos con la clase
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    //metodo para enviar el correo
    public void enviarCorreoSimple(String destinatario, String asunto, String mensaje) {
        try {
            //se establece la estructura basica de un correo
            SimpleMailMessage email = new SimpleMailMessage();
            
            // Usamos el correo ficticio para el entorno local
            //remitente del correo
            email.setFrom(remitentePorDefecto); 
            //destinatario del correo
            email.setTo(destinatario);
            //asunto del correo
            email.setSubject(asunto);
            //cuerpo del mensaje
            email.setText(mensaje);
            //envio del correo
            mailSender.send(email);
            //correo exitoso
            System.out.println("¡Correo enviado exitosamente a " + destinatario + "!");
            
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
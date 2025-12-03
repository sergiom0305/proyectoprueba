package com.proyecto_canal.demo.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Servicio para el manejo de envío de correos electrónicos, 
 * incluyendo códigos de verificación (OTP).
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    // El correo desde donde se envían los mensajes (debe coincidir con spring.mail.username)
    private static final String SENDER_EMAIL = "sergiomontiel0305@gmail.com"; 

    /**
     * Genera un código OTP de 6 dígitos.
     * @return El código OTP como String.
     */
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000); // Genera número entre 100000 y 999999
        return String.valueOf(number);
    }

    /**
     * Método genérico para enviar un correo simple.
     * @param to Dirección de destino.
     * @param subject Asunto del correo.
     * @param text Cuerpo del mensaje.
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER_EMAIL); 
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Correo enviado a: " + to + " con asunto: " + subject);
        } catch (Exception e) {
            System.err.println("❌ ERROR al enviar correo a " + to + ": " + e.getMessage());
            throw new RuntimeException("Fallo al enviar correo electrónico. Verifique la configuración y credenciales de Spring Mail.");
        }
    }

    /**
     * Envía un correo de verificación con un código OTP.
     * @param to Correo del usuario a verificar.
     * @param otp Código de verificación.
     */
    public void sendVerificationEmail(String to, String otp) {
        String subject = "Código de Verificación - GlobalTech";
        String body = String.format(
            "Gracias por registrarte en GlobalTech.\n\n" +
            "Tu código de verificación es: %s\n\n" +
            "Este código expira en 5 minutos. Por favor, ingrésalo en la página de registro para activar tu cuenta.\n\n" +
            "Saludos,\nEl equipo de GlobalTech",
            otp
        );
        sendEmail(to, subject, body);
    }
}

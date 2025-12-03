package com.proyecto_canal.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // El correo del administrador que recibe las ALARMAS. ¡REEMPLAZAR!
    private static final String ADMIN_EMAIL = "admin.globaltech@tuempresa.com"; 
    // El correo desde donde se envían los mensajes (debe coincidir con spring.mail.username)
    private static final String SENDER_EMAIL = "tu_correo@gmail.com"; 

    // Clase interna para guardar el OTP y su fecha de expiración
    private static class OtpEntry {
        final String otp;
        final LocalDateTime expiryTime;

        OtpEntry(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    // Almacenamiento temporal para los OTPs (email -> OtpEntry)
    private final Map<String, OtpEntry> otpStorage = new HashMap<>();

    /**
     * Genera un código OTP de 6 dígitos.
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Envía un correo electrónico con un código OTP que dura 30 minutos.
     * @param toEmail La dirección de correo del destinatario.
     */
    public void sendOtp(String toEmail) {
        String otp = generateOtp();
        
        // Define el tiempo de expiración: 30 minutos a partir de ahora
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30);
        
        // Guarda el OTP y su tiempo de expiración en el almacenamiento
        otpStorage.put(toEmail, new OtpEntry(otp, expiryTime)); 

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER_EMAIL); 
        message.setTo(toEmail);
        message.setSubject("Código de Verificación Temporal");
        message.setText("Tu Código de Verificación es: " + otp + 
                        "\nEste código expirará el: " + expiryTime + 
                        "\nTiene 30 minutos para usarlo.");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo. Revise la configuración SMTP: " + e.getMessage());
        }
    }

    /**
     * Verifica si el OTP es correcto y no ha expirado para un email dado.
     */
    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpStorage.get(email);
        
        if (entry == null) {
            return false; // No hay OTP registrado para ese email
        }

        if (entry.isExpired()) {
            otpStorage.remove(email); // Limpia OTP expirado
            return false; // El OTP ha expirado
        }
        
        // Compara el OTP ingresado con el almacenado
        return otp.equals(entry.otp);
    }
    
    /**
     * Elimina el OTP después de la verificación (o si se desea limpiarlo).
     */
    public void clearOtp(String email) {
        otpStorage.remove(email);
    }
    
    /**
     * Envía un correo de ALERTA CRÍTICA al administrador.
     * @param sensorId ID del sensor que generó la alerta.
     * @param nivelCm Nivel de agua registrado.
     */
    public void sendHighAlertEmail(String sensorId, Integer nivelCm) {
        String subject = String.format("🚨 ALERTA CRÍTICA: Desbordamiento Potencial en Sensor %s", sensorId);
        String body = String.format(
            "¡ATENCIÓN ADMINISTRADOR!\n\n" +
            "El sensor con ID: %s ha detectado un nivel de agua CRÍTICO.\n" +
            "Nivel Registrado: %d cm.\n" +
            "Tipo de Alerta: ALTA.\n\n" +
            "Por favor, toma acción inmediata para verificar el estado del canal.\n" +
            "Fecha y Hora de la Alerta: %s\n\n" +
            "Sistema de Monitoreo GlobalTech",
            sensorId,
            nivelCm,
            LocalDateTime.now()
        );
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER_EMAIL); 
        message.setTo(ADMIN_EMAIL);
        message.setSubject(subject);
        message.setText(body);
        
        try {
            mailSender.send(message);
            System.out.println("✅ ALERTA: Correo enviado a: " + ADMIN_EMAIL + " con asunto: " + subject);
        } catch (Exception e) {
            System.err.println("❌ ERROR al enviar correo de ALERTA a " + ADMIN_EMAIL + ": " + e.getMessage());
            // En un ambiente de producción, podrías querer registrar el error
        }
    }
}

package com.fiberplus.main.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Código de recuperación de contraseña");
            helper.setText(buildEmailTemplate(code), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

    private String buildEmailTemplate(String code) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 50px auto;
                            background-color: #ffffff;
                            padding: 30px;
                            border-radius: 10px;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        .header {
                            text-align: center;
                            color: #333;
                        }
                        .code-box {
                            background-color: #f0f0f0;
                            border: 2px dashed #4CAF50;
                            border-radius: 8px;
                            padding: 20px;
                            text-align: center;
                            margin: 30px 0;
                        }
                        .code {
                            font-size: 32px;
                            font-weight: bold;
                            color: #4CAF50;
                            letter-spacing: 5px;
                        }
                        .message {
                            color: #666;
                            line-height: 1.6;
                            text-align: center;
                        }
                        .warning {
                            color: #ff6b6b;
                            font-size: 14px;
                            text-align: center;
                            margin-top: 20px;
                        }
                        .footer {
                            text-align: center;
                            color: #999;
                            font-size: 12px;
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #eee;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Recuperación de Contraseña</h1>
                        </div>

                        <div class="message">
                            <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
                            <p>Utiliza el siguiente código para continuar:</p>
                        </div>

                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>

                        <div class="message">
                            <p>Este código expirará en <strong>10 minutos</strong>.</p>
                        </div>

                        <div class="warning">
                            ⚠️ Si no solicitaste este cambio, ignora este correo.
                        </div>

                        <div class="footer">
                            <p>FiberPlus - Sistema de Gestión</p>
                            <p>Este es un correo automático, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(code);
    }
}

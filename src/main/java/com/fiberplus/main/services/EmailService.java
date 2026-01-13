package com.fiberplus.main.services;

import java.time.LocalDateTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
            helper.setText(buildPasswordResetTemplate(code), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

    @Async
    public void sendTaskAssignmentEmail(String toEmail, String userName, String taskTitle, String taskDescription, String priority, LocalDateTime dueDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎯 Nueva tarea asignada: " + taskTitle);
            helper.setText(buildTaskAssignmentTemplate(userName, taskTitle, taskDescription, priority, dueDate), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }
    
    @Async
    public void sendTaskUpdateEmail(String toEmail, String userName, String taskTitle, String changes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔄 Tarea actualizada: " + taskTitle);
            helper.setText(buildTaskUpdateTemplate(userName, taskTitle, changes), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }
    
    @Async
    public void sendTaskMovedEmail(String toEmail, String userName, String taskTitle, String oldBoard, String newBoard) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("📋 Tarea movida: " + taskTitle);
            helper.setText(buildTaskMovedTemplate(userName, taskTitle, oldBoard, newBoard), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

    @Async
    public void sendTaskCompletedEmail(String toEmail, String userName, String taskTitle, String completedBy) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("✅ Tarea completada: " + taskTitle);
            helper.setText(buildTaskCompletedTemplate(userName, taskTitle, completedBy), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

    // ==================== TEMPLATES ====================

    private String buildPasswordResetTemplate(String code) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            padding: 40px 30px;
                            text-align: center;
                            color: white;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .code-box {
                            background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                            border-radius: 12px;
                            padding: 30px;
                            text-align: center;
                            margin: 30px 0;
                            border: 3px solid #667eea;
                        }
                        .code {
                            font-size: 40px;
                            font-weight: bold;
                            color: #667eea;
                            letter-spacing: 8px;
                            font-family: 'Courier New', monospace;
                        }
                        .message {
                            color: #4a5568;
                            line-height: 1.8;
                            text-align: center;
                            font-size: 16px;
                        }
                        .warning {
                            background-color: #fff5f5;
                            border-left: 4px solid #fc8181;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 8px;
                        }
                        .warning p {
                            margin: 0;
                            color: #c53030;
                            font-size: 14px;
                        }
                        .footer {
                            background-color: #f7fafc;
                            text-align: center;
                            padding: 30px;
                            color: #718096;
                            font-size: 13px;
                            border-top: 1px solid #e2e8f0;
                        }
                        .footer p {
                            margin: 5px 0;
                        }
                        .highlight {
                            color: #667eea;
                            font-weight: 600;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 Recuperación de Contraseña</h1>
                        </div>

                        <div class="content">
                            <div class="message">
                                <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
                                <p>Utiliza el siguiente código para continuar:</p>
                            </div>

                            <div class="code-box">
                                <div class="code">%s</div>
                            </div>

                            <div class="message">
                                <p>Este código expirará en <span class="highlight">10 minutos</span>.</p>
                            </div>

                            <div class="warning">
                                <p>⚠️ Si no solicitaste este cambio, ignora este correo y tu contraseña permanecerá segura.</p>
                            </div>
                        </div>

                        <div class="footer">
                            <p><strong>FiberPlus</strong> - Sistema de Gestión de Tareas</p>
                            <p>Este es un correo automático, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(code);
    }

    private String buildTaskAssignmentTemplate(String userName, String taskTitle, String taskDescription, String priority, LocalDateTime dueDate) {
        String priorityColor = getPriorityColor(priority);
        String priorityLabel = getPriorityLabel(priority);
        
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            padding: 40px 30px;
                            text-align: center;
                            color: white;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .greeting {
                            font-size: 18px;
                            color: #2d3748;
                            margin-bottom: 20px;
                        }
                        .task-card {
                            background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                            border-radius: 12px;
                            padding: 25px;
                            margin: 25px 0;
                            border-left: 5px solid #667eea;
                        }
                        .task-title {
                            font-size: 22px;
                            font-weight: bold;
                            color: #2d3748;
                            margin-bottom: 15px;
                        }
                        .task-description {
                            color: #4a5568;
                            line-height: 1.6;
                            margin-bottom: 20px;
                        }
                        .task-details {
                            display: table;
                            width: 100%%;
                        }
                        .detail-row {
                            display: table-row;
                        }
                        .detail-label {
                            display: table-cell;
                            padding: 8px 0;
                            font-weight: 600;
                            color: #4a5568;
                            width: 120px;
                        }
                        .detail-value {
                            display: table-cell;
                            padding: 8px 0;
                            color: #2d3748;
                        }
                        .priority-badge {
                            display: inline-block;
                            padding: 6px 12px;
                            border-radius: 20px;
                            font-size: 12px;
                            font-weight: bold;
                            color: white;
                        }
                        .cta-button {
                            display: inline-block;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white;
                            padding: 15px 40px;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 600;
                            margin-top: 20px;
                            text-align: center;
                        }
                        .footer {
                            background-color: #f7fafc;
                            text-align: center;
                            padding: 30px;
                            color: #718096;
                            font-size: 13px;
                            border-top: 1px solid #e2e8f0;
                        }
                        .footer p {
                            margin: 5px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎯 Nueva Tarea Asignada</h1>
                        </div>

                        <div class="content">
                            <div class="greeting">
                                Hola <strong>%s</strong>,
                            </div>

                            <p style="color: #4a5568; line-height: 1.6;">
                                Se te ha asignado una nueva tarea. Por favor revisa los detalles a continuación:
                            </p>

                            <div class="task-card">
                                <div class="task-title">📝 %s</div>
                                
                                <div class="task-description">
                                    %s
                                </div>

                                <div class="task-details">
                                    <div class="detail-row">
                                        <div class="detail-label">🔥 Prioridad:</div>
                                        <div class="detail-value">
                                            <span class="priority-badge" style="background-color: %s;">%s</span>
                                        </div>
                                    </div>
                                    <div class="detail-row">
                                        <div class="detail-label">📅 Fecha límite:</div>
                                        <div class="detail-value">%s</div>
                                    </div>
                                </div>
                            </div>

                            <div style="text-align: center;">
                                <a href="#" class="cta-button">Ver Tarea en la Plataforma</a>
                            </div>
                        </div>

                        <div class="footer">
                            <p><strong>FiberPlus</strong> - Sistema de Gestión de Tareas</p>
                            <p>Este es un correo automático, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    userName, 
                    taskTitle, 
                    taskDescription != null ? taskDescription : "Sin descripción adicional",
                    priorityColor,
                    priorityLabel,
                    dueDate != null ? dueDate : "No especificada"
                );
    }

    private String buildTaskUpdateTemplate(String userName, String taskTitle, String changes) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                        }
                        .header {
                            background: linear-gradient(135deg, #f59e0b 0%%, #ea580c 100%%);
                            padding: 40px 30px;
                            text-align: center;
                            color: white;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .greeting {
                            font-size: 18px;
                            color: #2d3748;
                            margin-bottom: 20px;
                        }
                        .update-card {
                            background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                            border-radius: 12px;
                            padding: 25px;
                            margin: 25px 0;
                            border-left: 5px solid #f59e0b;
                        }
                        .task-title {
                            font-size: 22px;
                            font-weight: bold;
                            color: #2d3748;
                            margin-bottom: 15px;
                        }
                        .changes-box {
                            background-color: white;
                            border-radius: 8px;
                            padding: 15px;
                            margin-top: 15px;
                            color: #4a5568;
                            line-height: 1.6;
                        }
                        .cta-button {
                            display: inline-block;
                            background: linear-gradient(135deg, #f59e0b 0%%, #ea580c 100%%);
                            color: white;
                            padding: 15px 40px;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 600;
                            margin-top: 20px;
                        }
                        .footer {
                            background-color: #f7fafc;
                            text-align: center;
                            padding: 30px;
                            color: #718096;
                            font-size: 13px;
                            border-top: 1px solid #e2e8f0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔄 Tarea Actualizada</h1>
                        </div>

                        <div class="content">
                            <div class="greeting">
                                Hola <strong>%s</strong>,
                            </div>

                            <p style="color: #4a5568; line-height: 1.6;">
                                Una tarea asignada a ti ha sido actualizada:
                            </p>

                            <div class="update-card">
                                <div class="task-title">📝 %s</div>
                                
                                <div class="changes-box">
                                    <strong>Cambios realizados:</strong><br/>
                                    %s
                                </div>
                            </div>

                            <div style="text-align: center;">
                                <a href="#" class="cta-button">Ver Detalles Completos</a>
                            </div>
                        </div>

                        <div class="footer">
                            <p><strong>FiberPlus</strong> - Sistema de Gestión de Tareas</p>
                            <p>Este es un correo automático, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, taskTitle, changes);
    }

    private String buildTaskMovedTemplate(String userName, String taskTitle, String oldBoard, String newBoard) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                        }
                        .header {
                            background: linear-gradient(135deg, #06b6d4 0%%, #0891b2 100%%);
                            padding: 40px 30px;
                            text-align: center;
                            color: white;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .greeting {
                            font-size: 18px;
                            color: #2d3748;
                            margin-bottom: 20px;
                        }
                        .move-card {
                            background: linear-gradient(135deg, #cffafe 0%%, #a5f3fc 100%%);
                            border-radius: 12px;
                            padding: 25px;
                            margin: 25px 0;
                            border-left: 5px solid #06b6d4;
                        }
                        .task-title {
                            font-size: 22px;
                            font-weight: bold;
                            color: #2d3748;
                            margin-bottom: 20px;
                        }
                        .board-transition {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 15px;
                            margin: 20px 0;
                        }
                        .board-box {
                            background-color: white;
                            padding: 15px 25px;
                            border-radius: 8px;
                            font-weight: 600;
                            color: #2d3748;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                        }
                        .arrow {
                            font-size: 24px;
                            color: #06b6d4;
                        }
                        .cta-button {
                            display: inline-block;
                            background: linear-gradient(135deg, #06b6d4 0%%, #0891b2 100%%);
                            color: white;
                            padding: 15px 40px;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 600;
                            margin-top: 20px;
                        }
                        .footer {
                            background-color: #f7fafc;
                            text-align: center;
                            padding: 30px;
                            color: #718096;
                            font-size: 13px;
                            border-top: 1px solid #e2e8f0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📋 Tarea Movida</h1>
                        </div>

                        <div class="content">
                            <div class="greeting">
                                Hola <strong>%s</strong>,
                            </div>

                            <p style="color: #4a5568; line-height: 1.6;">
                                Una tarea asignada a ti ha sido movida a un nuevo tablero:
                            </p>

                            <div class="move-card">
                                <div class="task-title">📝 %s</div>
                                
                                <div class="board-transition">
                                    <div class="board-box">%s</div>
                                    <div class="arrow">→</div>
                                    <div class="board-box" style="background: linear-gradient(135deg, #06b6d4 0%%, #0891b2 100%%); color: white;">%s</div>
                                </div>
                            </div>

                            <div style="text-align: center;">
                                <a href="#" class="cta-button">Ver Tarea</a>
                            </div>
                        </div>

                        <div class="footer">
                            <p><strong>FiberPlus</strong> - Sistema de Gestión de Tareas</p>
                            <p>Este es un correo automático, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    userName, 
                    taskTitle, 
                    oldBoard != null ? oldBoard : "Tablero anterior",
                    newBoard
                );
    }

    private String buildTaskCompletedTemplate(String userName, String taskTitle, String completedBy) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                        }
                        .header {
                            background: linear-gradient(135deg, #10b981 0%%, #059669 100%%);
                            padding: 40px 30px;
                            text-align: center;
                            color: white;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .greeting {
                            font-size: 18px;
                            color: #2d3748;
                            margin-bottom: 20px;
                        }
                        .completed-card {
                            background: linear-gradient(135deg, #d1fae5 0%%, #a7f3d0 100%%);
                            border-radius: 12px;
                            padding: 25px;
                            margin: 25px 0;
                            border-left: 5px solid #10b981;
                            text-align: center;
                        }
                        .checkmark {
                            font-size: 60px;
                            margin-bottom: 15px;
                        }
                        .task-title {
                            font-size: 22px;
                            font-weight: bold;
                            color: #2d3748;
                            margin-bottom: 15px;
                        }
                        .completed-by {
                            background-color: white;
                            border-radius: 8px;
                            padding: 15px;
                            margin-top: 15px;
                            color: #4a5568;
                        }
                        .cta-button {
                            display: inline-block;
                            background: linear-gradient(135deg, #10b981 0%%, #059669 100%%);
                            color: white;
                            padding: 15px 40px;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 600;
                            margin-top: 20px;
                        }
                        .footer {
                            background-color: #f7fafc;
                            text-align: center;
                            padding: 30px;
                            color: #718096;
                            font-size: 13px;
                            border-top: 1px solid #e2e8f0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✅ Tarea Completada</h1>
                        </div>

                        <div class="content">
                            <div class="greeting">
                                Hola <strong>%s</strong>,
                            </div>

                            <p style="color: #4a5568; line-height: 1.6;">
                                Una tarea en la que estabas involucrado ha sido completada:
                            </p>

                            <div class="completed-card">
                                <div class="checkmark">✓</div>
                                <div class="task-title">📝 %s</div>
                                
                                <div class="completed-by">
                                    <strong>Completada por:</strong> %s
                                </div>
                            </div>

                            <div style="text-align: center;">
                                <a href="#" class="cta-button">Ver Evidencias</a>
                            </div>
                        </div>

                        <div class="footer">
                            <p><strong>FiberPlus</strong> - Sistema de Gestión de Tareas</p>
                            <p>Este es un correo automático, por favor no responder.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(userName, taskTitle, completedBy);
    }

    // ==================== HELPER METHODS ====================

    private String getPriorityColor(String priority) {
        if (priority == null) return "#6b7280";
        
        return switch (priority.toUpperCase()) {
            case "HIGH" -> "#ef4444";
            case "MEDIUM" -> "#f59e0b";
            case "LOW" -> "#10b981";
            default -> "#6b7280";
        };
    }

    private String getPriorityLabel(String priority) {
        if (priority == null) return "Normal";
        
        return switch (priority.toUpperCase()) {
            case "HIGH" -> "Alta";
            case "MEDIUM" -> "Media";
            case "LOW" -> "Baja";
            default -> "Normal";
        };
    }
}
package com.fiberplus.main.controllers.notification;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.NotificationDto;
import com.fiberplus.main.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
@Tag(name = "Notifications", description = "API para gestión de notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las notificaciones de un usuario")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable String userId) {
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return ResponseBuilder.ok("Notificaciones obtenidas exitosamente", notifications);
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Obtener notificaciones no leídas de un usuario")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable String userId) {
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseBuilder.ok("Notificaciones no leídas obtenidas exitosamente", notifications);
    }

    @GetMapping("/user/{userId}/unread/count")
    @Operation(summary = "Obtener cantidad de notificaciones no leídas")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable String userId) {
        Long count = notificationService.getUnreadCount(userId);
        return ResponseBuilder.ok("Cantidad de notificaciones no leídas", count);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @Parameter(description = "ID de la notificación", required = true)
            @PathVariable String id) {
        NotificationDto notification = notificationService.markAsRead(id);
        return ResponseBuilder.ok("Notificación marcada como leída", notification);
    }

    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseBuilder.ok("Todas las notificaciones marcadas como leídas", null);
    }
}    


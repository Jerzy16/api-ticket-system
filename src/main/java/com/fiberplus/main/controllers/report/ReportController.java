package com.fiberplus.main.controllers.report;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.report.ReportDto;
import com.fiberplus.main.services.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/reports")
@Tag(name = "Reports", description = "API para generación de reportes con evidencias")
public class ReportController {
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    @Operation(summary = "Generar reporte", description = "Genera un reporte según tipo y rango de fechas")
    public ResponseEntity<ApiResponse<ReportDto>> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "GENERAL") String reportType) {
        
        ReportDto report = reportService.generateReport(startDate, endDate, reportType);
        return ResponseBuilder.ok("Reporte generado exitosamente", report);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Reporte por usuario", description = "Genera reporte de un usuario específico")
    public ResponseEntity<ApiResponse<ReportDto>> getUserReport(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        ReportDto report = reportService.generateUserReport(userId, startDate, endDate);
        return ResponseBuilder.ok("Reporte de usuario generado", report);
    }

    @GetMapping("/board/{boardId}")
    @Operation(summary = "Reporte por tablero", description = "Genera reporte de un tablero específico")
    public ResponseEntity<ApiResponse<ReportDto>> getBoardReport(
            @PathVariable String boardId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        ReportDto report = reportService.generateBoardReport(boardId, startDate, endDate);
        return ResponseBuilder.ok("Reporte de tablero generado", report);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard general", description = "Estadísticas generales en tiempo real")
    public ResponseEntity<ApiResponse<ReportDto>> getDashboard() {
        ReportDto dashboard = reportService.generateDashboard();
        return ResponseBuilder.ok("Dashboard generado", dashboard);
    }
}
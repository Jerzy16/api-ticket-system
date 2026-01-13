package com.fiberplus.main.controllers.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fiberplus.main.common.ApiResponse;
import com.fiberplus.main.common.ResponseBuilder;
import com.fiberplus.main.dtos.CloudinaryResponseDto;
import com.fiberplus.main.services.CloudinaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/upload")
@Tag(name = "Upload", description = "API para subir archivos a Cloudinary")
public class ImageUploadController {
    private final CloudinaryService cloudinaryService;
    
    private static final String TASK_EVIDENCE_FOLDER = "fiberplus/task-evidences";
    
    public ImageUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/image")
    @Operation(summary = "Subir una imagen", description = "Sube una sola imagen a Cloudinary y retorna la URL")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        
        CloudinaryResponseDto result = cloudinaryService.uploadImageWithTransformation(
            file, 
            TASK_EVIDENCE_FOLDER, 
            1200,  // ancho máximo
            1200   // alto máximo
        );
        
        return ResponseBuilder.ok(
            "Imagen subida exitosamente", 
            Map.of("url", result.getSecureUrl())
        );
    }

    @PostMapping("/images")
    @Operation(summary = "Subir múltiples imágenes", description = "Sube múltiples imágenes a Cloudinary y retorna las URLs")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files) {
        
        List<String> urls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            CloudinaryResponseDto result = cloudinaryService.uploadImageWithTransformation(
                file, 
                TASK_EVIDENCE_FOLDER, 
                1200, 
                1200
            );
            urls.add(result.getSecureUrl());
        }
        
        return ResponseBuilder.ok(
            "Imágenes subidas exitosamente", 
            Map.of("urls", urls)
        );
    }

    @DeleteMapping("/image")
    @Operation(summary = "Eliminar una imagen", description = "Elimina una imagen de Cloudinary usando su public_id")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@RequestParam("publicId") String publicId) {
        cloudinaryService.deleteImage(publicId);
        return ResponseBuilder.ok("Imagen eliminada exitosamente", null);
    }
}

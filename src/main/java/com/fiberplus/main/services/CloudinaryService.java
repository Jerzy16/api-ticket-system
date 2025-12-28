package com.fiberplus.main.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.fiberplus.main.dtos.CloudinaryResponseDto;
import com.fiberplus.main.exception.GenericException;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public CloudinaryResponseDto uploadImage(MultipartFile file, String folder) {
        try {
            validateFile(file);

            String publicId = generatePublicId();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder != null ? folder : "fiberplus",
                    "resource_type", "image",
                    "overwrite", true,
                    "transformation", ObjectUtils.asMap(
                            "quality", "auto",
                            "fetch_format", "auto"));

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            throw new GenericException("Error al subir la imagen: " + e.getMessage());
        }
    }

    public CloudinaryResponseDto uploadImageWithTransformation(
            MultipartFile file,
            String folder,
            Integer width,
            Integer height) {
        try {
            validateFile(file);

            String publicId = generatePublicId();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder != null ? folder : "fiberplus",
                    "resource_type", "image",
                    "overwrite", true,
                    "transformation", ObjectUtils.asMap(
                            "width", width,
                            "height", height,
                            "crop", "limit",
                            "quality", "auto",
                            "fetch_format", "auto"));

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            throw new GenericException("Error al subir la imagen: " + e.getMessage());
        }
    }

    public boolean deleteImage(String publicId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            throw new GenericException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    public boolean deleteImages(String[] publicIds) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.api().deleteResources(
                    java.util.Arrays.asList(publicIds),
                    ObjectUtils.emptyMap());
            return result != null;
        } catch (Exception e) {
            throw new GenericException("Error al eliminar las imágenes: " + e.getMessage());
        }
    }

    public CloudinaryResponseDto uploadFile(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                throw new GenericException("El archivo está vacío");
            }

            String publicId = generatePublicId();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder != null ? folder : "fiberplus/documents",
                    "resource_type", "raw",
                    "overwrite", true);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            throw new GenericException("Error al subir el archivo: " + e.getMessage());
        }
    }

    public String getOptimizedUrl(String publicId, Integer width, Integer height) {
        Transformation transformation = new Transformation()
                .width(width)
                .height(height)
                .crop("fill")
                .quality("auto")
                .fetchFormat("auto");

        return cloudinary.url()
                .transformation(transformation)
                .generate(publicId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new GenericException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GenericException("El archivo debe ser una imagen");
        }

        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new GenericException("El archivo excede el tamaño máximo permitido (10MB)");
        }
    }

    private String generatePublicId() {
        return UUID.randomUUID().toString();
    }

    private CloudinaryResponseDto mapToDTO(Map<String, Object> uploadResult) {
        return CloudinaryResponseDto.builder()
                .publicId((String) uploadResult.get("public_id"))
                .url((String) uploadResult.get("url"))
                .secureUrl((String) uploadResult.get("secure_url"))
                .format((String) uploadResult.get("format"))
                .resourceType((String) uploadResult.get("resource_type"))
                .bytes(uploadResult.get("bytes") != null ? ((Number) uploadResult.get("bytes")).longValue() : null)
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .build();
    }
}
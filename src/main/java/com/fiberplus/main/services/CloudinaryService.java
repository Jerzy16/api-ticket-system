package com.fiberplus.main.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.fiberplus.main.dtos.CloudinaryResponseDto;
import com.fiberplus.main.exception.GenericException;

@Service
public class CloudinaryService {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;

    private static final String DEFAULT_FOLDER = "fiberplus";
    private static final String DOCUMENTS_FOLDER = "fiberplus/documents";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/jpg");

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public CloudinaryResponseDto uploadImage(MultipartFile file, String folder) {
        try {
            validateImageFile(file);

            String publicId = generatePublicId();
            logger.info("Subiendo imagen: {} a folder: {}", publicId, folder);

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder != null ? folder : DEFAULT_FOLDER,
                    "resource_type", "image",
                    "overwrite", true,
                    "transformation", ObjectUtils.asMap(
                            "quality", "auto",
                            "fetch_format", "auto"));

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            logger.info("Imagen subida exitosamente: {}", uploadResult.get("secure_url"));
            return mapToDTO(uploadResult);

        } catch (IOException e) {
            logger.error("Error al subir imagen: {}", e.getMessage());
            throw new GenericException("Error al subir la imagen: " + e.getMessage());
        }
    }

    public CloudinaryResponseDto uploadImageWithTransformation(
            MultipartFile file,
            String folder,
            Integer width,
            Integer height) {

        try {
            validateImageFile(file);

            String publicId = generatePublicId();
            logger.info("Subiendo imagen con transformación {}x{}: {}", width, height, publicId);

            Transformation transformation = new Transformation()
                    .width(width)
                    .height(height)
                    .crop("limit")
                    .quality("auto")
                    .fetchFormat("auto");

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder != null ? folder : DEFAULT_FOLDER,
                    "resource_type", "image",
                    "overwrite", true,
                    "transformation", transformation);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            logger.info("Imagen con transformación subida exitosamente: {}",
                    uploadResult.get("secure_url"));

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            logger.error("Error al subir imagen con transformación: {}", e.getMessage());
            throw new GenericException("Error al subir la imagen: " + e.getMessage());
        }
    }

    public CloudinaryResponseDto uploadFile(MultipartFile file, String folder) {
        try {
            validateGeneralFile(file);

            String publicId = generatePublicId();
            logger.info("Subiendo archivo: {} a folder: {}", publicId, folder);

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder != null ? folder : DOCUMENTS_FOLDER,
                    "resource_type", "raw",
                    "overwrite", true);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            logger.info("Archivo subido exitosamente: {}", uploadResult.get("secure_url"));
            return mapToDTO(uploadResult);

        } catch (IOException e) {
            logger.error("Error al subir archivo: {}", e.getMessage());
            throw new GenericException("Error al subir el archivo: " + e.getMessage());
        }
    }

    public boolean deleteImage(String publicId) {
        try {
            if (publicId == null || publicId.trim().isEmpty()) {
                throw new GenericException("El publicId no puede estar vacío");
            }

            logger.info("Eliminando imagen: {}", publicId);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            String resultStatus = (String) result.get("result");

            if ("ok".equals(resultStatus)) {
                logger.info("Imagen eliminada exitosamente: {}", publicId);
                return true;
            } else if ("not found".equals(resultStatus)) {
                logger.warn("La imagen no existe en Cloudinary: {}", publicId);
                throw new GenericException("La imagen no existe en Cloudinary");
            }

            logger.warn("No se pudo eliminar la imagen: {}. Resultado: {}", publicId, resultStatus);
            return false;

        } catch (IOException e) {
            logger.error("Error al eliminar imagen {}: {}", publicId, e.getMessage());
            throw new GenericException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    public boolean deleteImages(String[] publicIds) {
        try {
            if (publicIds == null || publicIds.length == 0) {
                throw new GenericException("Debe proporcionar al menos un publicId");
            }

            logger.info("Eliminando {} imágenes", publicIds.length);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.api().deleteResources(
                    Arrays.asList(publicIds),
                    ObjectUtils.emptyMap());

            boolean success = result != null && !result.isEmpty();

            if (success) {
                logger.info("Imágenes eliminadas exitosamente");
            } else {
                logger.warn("No se pudieron eliminar algunas imágenes");
            }

            return success;

        } catch (Exception e) {
            logger.error("Error al eliminar imágenes: {}", e.getMessage());
            throw new GenericException("Error al eliminar las imágenes: " + e.getMessage());
        }
    }

    public String getOptimizedUrl(String publicId, Integer width, Integer height) {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new GenericException("El publicId no puede estar vacío");
        }

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

    public Map<String, Object> getImageInfo(String publicId) {
        try {
            if (publicId == null || publicId.trim().isEmpty()) {
                throw new GenericException("El publicId no puede estar vacío");
            }

            logger.info("Obteniendo información de imagen: {}", publicId);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());

            return result;

        } catch (Exception e) {
            logger.error("Error al obtener información de imagen {}: {}", publicId, e.getMessage());
            throw new GenericException("Error al obtener información de la imagen: " + e.getMessage());
        }
    }

    public Map<String, Object> listImages(String folder, int maxResults) {
        try {
            logger.info("Listando imágenes de folder: {} (max: {})", folder, maxResults);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.api().resources(
                    ObjectUtils.asMap(
                            "type", "upload",
                            "prefix", folder,
                            "max_results", maxResults));

            return result;

        } catch (Exception e) {
            logger.error("Error al listar imágenes del folder {}: {}", folder, e.getMessage());
            throw new GenericException("Error al listar imágenes: " + e.getMessage());
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GenericException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new GenericException(
                    "Formato de imagen no permitido. Use: JPEG, PNG, GIF o WebP");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GenericException(
                    String.format("El archivo excede el tamaño máximo permitido (%dMB)",
                            MAX_FILE_SIZE / (1024 * 1024)));
        }

        logger.debug("Validación de imagen exitosa: {} bytes, tipo: {}",
                file.getSize(), contentType);
    }

    private void validateGeneralFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GenericException("El archivo está vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GenericException(
                    String.format("El archivo excede el tamaño máximo permitido (%dMB)",
                            MAX_FILE_SIZE / (1024 * 1024)));
        }

        logger.debug("Validación de archivo exitosa: {} bytes, tipo: {}",
                file.getSize(), file.getContentType());
    }

    private boolean isValidImageType(String contentType) {
        return ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
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
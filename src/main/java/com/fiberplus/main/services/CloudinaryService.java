package com.fiberplus.main.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.fiberplus.main.exception.GenericException;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Sube una imagen a Cloudinary
     * 
     * @param file   Archivo a subir
     * @param folder Carpeta en Cloudinary (opcional)
     * @return Información del archivo subido
     */
    public CloudinaryResponseDTO uploadImage(MultipartFile file, String folder) {
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

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            throw new GenericException("Error al subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Sube una imagen con transformaciones específicas
     * 
     * @param file   Archivo a subir
     * @param folder Carpeta en Cloudinary
     * @param width  Ancho deseado
     * @param height Alto deseado
     * @return Información del archivo subido
     */
    public CloudinaryResponseDTO uploadImageWithTransformation(
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

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            throw new GenericException("Error al subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de Cloudinary
     * 
     * @param publicId ID público del archivo en Cloudinary
     * @return true si se eliminó correctamente
     */
    public boolean deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            throw new GenericException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    /**
     * Elimina múltiples imágenes de Cloudinary
     * 
     * @param publicIds Array de IDs públicos
     * @return true si se eliminaron correctamente
     */
    public boolean deleteImages(String[] publicIds) {
        try {
            Map result = cloudinary.api().deleteResources(
                    java.util.Arrays.asList(publicIds),
                    ObjectUtils.emptyMap());
            return result != null;
        } catch (Exception e) {
            throw new GenericException("Error al eliminar las imágenes: " + e.getMessage());
        }
    }

    /**
     * Sube un archivo (PDF, documentos, etc.)
     * 
     * @param file   Archivo a subir
     * @param folder Carpeta en Cloudinary
     * @return Información del archivo subido
     */
    public CloudinaryResponseDTO uploadFile(MultipartFile file, String folder) {
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

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToDTO(uploadResult);

        } catch (IOException e) {
            throw new GenericException("Error al subir el archivo: " + e.getMessage());
        }
    }

    /**
     * Obtiene la URL optimizada de una imagen
     * 
     * @param publicId ID público del archivo
     * @param width    Ancho deseado
     * @param height   Alto deseado
     * @return URL optimizada
     */
    public String getOptimizedUrl(String publicId, Integer width, Integer height) {
        return cloudinary.url()
                .transformation(ObjectUtils.asMap(
                        "width", width,
                        "height", height,
                        "crop", "fill",
                        "quality", "auto",
                        "fetch_format", "auto"))
                .generate(publicId);
    }

    // Métodos privados auxiliares

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new GenericException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GenericException("El archivo debe ser una imagen");
        }

        // Validar tamaño (por ejemplo, máximo 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new GenericException("El archivo excede el tamaño máximo permitido (10MB)");
        }
    }

    private String generatePublicId() {
        return UUID.randomUUID().toString();
    }

    private CloudinaryResponseDTO mapToDTO(Map uploadResult) {
        return CloudinaryResponseDTO.builder()
                .publicId((String) uploadResult.get("public_id"))
                .url((String) uploadResult.get("url"))
                .secureUrl((String) uploadResult.get("secure_url"))
                .format((String) uploadResult.get("format"))
                .resourceType((String) uploadResult.get("resource_type"))
                .bytes(((Number) uploadResult.get("bytes")).longValue())
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .build();
    }
}

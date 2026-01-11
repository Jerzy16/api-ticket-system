package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fiberplus.main.dtos.CloudinaryResponseDto;
import com.fiberplus.main.dtos.TeamGroupDto;
import com.fiberplus.main.dtos.TeamMemberDto;
import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.ConflictException;
import com.fiberplus.main.exception.GenericException;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final IUserRepository _repo;
    private final PasswordEncoder _passwordEncoder;
    private final CloudinaryService _cloudinaryService;
    
    private static final String PROFILE_PHOTOS_FOLDER = "fiberplus/profiles";

    public UserService(IUserRepository _repo, 
                      PasswordEncoder _passwordEncoder,
                      CloudinaryService _cloudinaryService) {
        this._repo = _repo;
        this._passwordEncoder = _passwordEncoder;
        this._cloudinaryService = _cloudinaryService;
    }

    public UserDto insert(UserDto dto) {
        _repo.findByEmail(dto.getEmail())
                .ifPresent(e -> {
                    throw new ConflictException(
                            "El usuario con el correo '" + dto.getEmail() + "' ya existe.");
                });

        _repo.findByUsername(dto.getUsername())
                .ifPresent(e -> {
                    throw new ConflictException(
                            "El usuario con el username '" + dto.getUsername() + "' ya existe.");
                });

        validateRoles(dto.getRoles());

        if (dto.getRoles().contains("SUPERADMIN")) {
            boolean superAdminExists = _repo.existsByRolesContaining("SUPERADMIN");
            if (superAdminExists) {
                throw new ConflictException("Ya existe un SUPERADMIN en el sistema");
            }
        }

        String id = UUID.randomUUID().toString();
        LocalDateTime date = LocalDateTime.now();

        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(_passwordEncoder.encode(dto.getPassword()));
        user.setRoles(dto.getRoles());
        user.setPosition(dto.getPosition());
        user.setPhoto(dto.getPhoto() != null ? dto.getPhoto() : getDefaultPhoto());
        user.setCreatedAt(date);
        user.setUpdatedAt(date);

        UserEntity savedUser = _repo.save(user);
        
        logger.info("Usuario creado exitosamente: {}", savedUser.getUsername());

        return mapToDto(savedUser);
    }

    public UserDto update(String id, UserDto dto) {
        UserEntity user = _repo.findById(id)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if (!user.getEmail().equals(dto.getEmail())) {
            _repo.findByEmail(dto.getEmail())
                    .ifPresent(e -> {
                        throw new ConflictException(
                                "El correo '" + dto.getEmail() + "' ya está en uso.");
                    });
        }

        if (!user.getUsername().equals(dto.getUsername())) {
            _repo.findByUsername(dto.getUsername())
                    .ifPresent(e -> {
                        throw new ConflictException(
                                "El username '" + dto.getUsername() + "' ya está en uso.");
                    });
        }

        validateRoles(dto.getRoles());

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(_passwordEncoder.encode(dto.getPassword()));
        }
        
        user.setRoles(dto.getRoles());
        user.setPosition(dto.getPosition());

        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            user.setPhoto(dto.getPhoto());
        }
        
        user.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = _repo.save(user);
        
        logger.info("Usuario actualizado exitosamente: {}", updatedUser.getUsername());

        return mapToDto(updatedUser);
    }

    public UserDto uploadProfilePhoto(String userId, MultipartFile file) {
        UserEntity user = _repo.findById(userId)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if (user.getPhoto() != null && !user.getPhoto().contains("ui-avatars.com")) {
            try {
                String oldPublicId = extractPublicIdFromUrl(user.getPhoto());
                if (oldPublicId != null) {
                    _cloudinaryService.deleteImage(oldPublicId);
                    logger.info("Foto anterior eliminada: {}", oldPublicId);
                }
            } catch (Exception e) {
                logger.warn("No se pudo eliminar la foto anterior: {}", e.getMessage());
            }
        }

        CloudinaryResponseDto uploadResult = _cloudinaryService.uploadImageWithTransformation(
            file, 
            PROFILE_PHOTOS_FOLDER, 
            400, 
            400
        );

        user.setPhoto(uploadResult.getSecureUrl());
        user.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = _repo.save(user);
        
        logger.info("Foto de perfil actualizada para usuario: {}", updatedUser.getUsername());

        return mapToDto(updatedUser);
    }

    public UserDto deleteProfilePhoto(String userId) {
        UserEntity user = _repo.findById(userId)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if (user.getPhoto() != null && !user.getPhoto().contains("ui-avatars.com")) {
            try {
                String publicId = extractPublicIdFromUrl(user.getPhoto());
                if (publicId != null) {
                    _cloudinaryService.deleteImage(publicId);
                    logger.info("Foto de perfil eliminada: {}", publicId);
                }
            } catch (Exception e) {
                logger.warn("No se pudo eliminar la foto de Cloudinary: {}", e.getMessage());
            }
        }

        user.setPhoto(getDefaultPhoto());
        user.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = _repo.save(user);
        
        logger.info("Foto de perfil restablecida a default para usuario: {}", updatedUser.getUsername());

        return mapToDto(updatedUser);
    }

    public void updatePassword(String userId, String currentPassword, String newPassword) {
        UserEntity user = _repo.findById(userId)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if (!_passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new GenericException("La contraseña actual es incorrecta");
        }

        user.setPassword(_passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        _repo.save(user);
        
        logger.info("Contraseña actualizada para usuario: {}", user.getUsername());
    }


    public void delete(String id) {
        UserEntity user = _repo.findById(id)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if (user.getRoles().contains("SUPERADMIN")) {
            long superAdminCount = _repo.countByRolesContaining("SUPERADMIN");
            if (superAdminCount <= 1) {
                throw new GenericException("No se puede eliminar el último SUPERADMIN del sistema");
            }
        }

        if (user.getPhoto() != null && !user.getPhoto().contains("ui-avatars.com")) {
            try {
                String publicId = extractPublicIdFromUrl(user.getPhoto());
                if (publicId != null) {
                    _cloudinaryService.deleteImage(publicId);
                    logger.info("Foto de perfil eliminada al borrar usuario: {}", publicId);
                }
            } catch (Exception e) {
                logger.warn("No se pudo eliminar la foto al borrar usuario: {}", e.getMessage());
            }
        }

        _repo.delete(user);
        
        logger.info("Usuario eliminado: {}", user.getUsername());
    }

    public UserDto getById(String id) {
        UserEntity user = _repo.findById(id)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));
        
        return mapToDto(user);
    }

    public List<UserDto> getAll() {
        List<UserEntity> users = _repo.findAll();
        
        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TeamGroupDto> getTeam() {
        List<UserEntity> users = _repo.findAll();

        Map<String, List<TeamMemberDto>> grouped = users.stream()
                .filter(user -> user.getPosition() != null && !user.getPosition().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        UserEntity::getPosition,
                        Collectors.mapping(user -> {
                            TeamMemberDto dto = new TeamMemberDto();
                            dto.setId(user.getId());
                            dto.setName(user.getName());
                            dto.setEmail(user.getEmail());
                            dto.setUsername(user.getUsername());
                            dto.setLastName(user.getLastname());
                            dto.setPost(user.getPosition());
                            dto.setRoles(user.getRoles());
                            dto.setPhoto(user.getPhoto() != null ? user.getPhoto() : getDefaultPhoto());
                            return dto;
                        }, Collectors.toList())));

        return grouped.entrySet().stream()
                .map(entry -> {
                    TeamGroupDto group = new TeamGroupDto();
                    group.setRole(entry.getKey());
                    group.setMembers(entry.getValue());
                    return group;
                })
                .toList();
    }

    private void validateRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new GenericException("Debe asignarse al menos un rol");
        }

        Set<String> allowedRoles = Set.of("SUPERADMIN", "ADMIN", "USER", "TECH");

        for (String role : roles) {
            if (!allowedRoles.contains(role)) {
                throw new GenericException("Rol no válido: " + role);
            }
        }
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            if (url == null || !url.contains("cloudinary.com")) {
                return null;
            }

            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }

            String afterUpload = url.substring(uploadIndex + 8); 
            
            if (afterUpload.startsWith("v")) {
                int slashIndex = afterUpload.indexOf("/");
                if (slashIndex != -1) {
                    afterUpload = afterUpload.substring(slashIndex + 1);
                }
            }

            int lastDotIndex = afterUpload.lastIndexOf(".");
            if (lastDotIndex != -1) {
                afterUpload = afterUpload.substring(0, lastDotIndex);
            }

            return afterUpload;
            
        } catch (Exception e) {
            logger.error("Error al extraer publicId de URL: {}", url, e);
            return null;
        }
    }

    private String getDefaultPhoto() {
        return "https://ui-avatars.com/api/?name=Usuario&background=random";
    }

    private UserDto mapToDto(UserEntity user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setLastName(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setPosition(user.getPosition());
        dto.setPhoto(user.getPhoto());
        dto.setRoles(user.getRoles());
        return dto;
    }
}
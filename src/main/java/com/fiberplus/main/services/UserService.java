package com.fiberplus.main.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiberplus.main.dtos.TeamGroupDto;
import com.fiberplus.main.dtos.TeamMemberDto;
import com.fiberplus.main.dtos.UserDto;
import com.fiberplus.main.entities.UserEntity;
import com.fiberplus.main.exception.ConflictException;
import com.fiberplus.main.exception.GenericException;
import com.fiberplus.main.repositories.IUserRepository;

@Service
public class UserService {

    private final IUserRepository _repo;
    private final PasswordEncoder _passwordEncoder;

    public UserService(IUserRepository _repo, PasswordEncoder _passwordEncoder) {
        this._repo = _repo;
        this._passwordEncoder = _passwordEncoder;
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

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new GenericException("Debe asignarse al menos un rol");
        }

        Set<String> allowedRoles = Set.of("SUPERADMIN", "ADMIN", "USER", "TECH");

        for (String role : dto.getRoles()) {
            if (!allowedRoles.contains(role)) {
                throw new GenericException("Rol no válido: " + role);
            }
        }

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
        user.setPhoto(dto.getPhoto());
        user.setCreatedAt(date);
        user.setUpdatedAt(date);

        UserEntity savedUser = _repo.save(user);

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

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new GenericException("Debe asignarse al menos un rol");
        }

        Set<String> allowedRoles = Set.of("SUPERADMIN", "ADMIN", "USER", "TECH");

        for (String role : dto.getRoles()) {
            if (!allowedRoles.contains(role)) {
                throw new GenericException("Rol no válido: " + role);
            }
        }

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(_passwordEncoder.encode(dto.getPassword()));
        }
        
        user.setRoles(dto.getRoles());
        user.setPosition(dto.getPosition());
        user.setPhoto(dto.getPhoto());
        user.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = _repo.save(user);

        return mapToDto(updatedUser);
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

        _repo.delete(user);
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
                            dto.setName(buildFullName(user));
                            dto.setPost(user.getPosition());
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

    private String buildFullName(UserEntity user) {
        StringBuilder name = new StringBuilder();
        
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            name.append(user.getName().trim());
        }
        
        if (user.getLastname() != null && !user.getLastname().trim().isEmpty()) {
            if (name.length() > 0) {
                name.append(" ");
            }
            name.append(user.getLastname().trim());
        }
        
        if (name.length() == 0) {
            if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                return user.getUsername();
            } else if (user.getEmail() != null) {
                return user.getEmail().split("@")[0];
            }
            return "Usuario sin nombre";
        }
        
        return name.toString();
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
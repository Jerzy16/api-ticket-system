package com.fiberplus.main.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "password_resets")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PasswordResetEntity extends BaseEntity{
    private String email;
    private String code;
    private LocalDateTime expiresAt;
    private boolean used;
}

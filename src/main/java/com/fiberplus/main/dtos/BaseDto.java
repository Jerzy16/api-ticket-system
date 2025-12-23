package com.fiberplus.main.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseDto {
    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean enabled = true;
}

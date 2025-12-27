package com.fiberplus.main.dtos;

import jakarta.validation.constraints.NotBlank;
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
public class BoardDto extends BaseDto {
    @NotBlank(message = "El campo titulo no puede estar vació.")
    private String title;
    private String createdBy;
    private String status;
}

package com.fiberplus.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryResponseDto {
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private String resourceType;
    private Long bytes;
    private Integer width;
    private Integer height;
}

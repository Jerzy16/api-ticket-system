package com.fiberplus.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AssignedUserDto {
    private String id;
    private String name;
    private String photo;
    private String position;
    
    public static AssignedUserDto fromUserEntity(String id, String name, String photo, String position) {
        return AssignedUserDto.builder()
                .id(id)
                .name(name)
                .photo(photo)
                .position(position)
                .build();
    }
}

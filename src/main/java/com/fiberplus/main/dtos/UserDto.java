package com.fiberplus.main.dtos;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class UserDto {

    private String username;

    private String email;

    private String password;

    private Set<String> roles = new HashSet<>();

}

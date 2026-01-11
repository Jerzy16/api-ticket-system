package com.fiberplus.main.dtos;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamMemberDto {
    private String id;
    private String username;
    private String name;
    private String lastName;
    private String email;
    private String post;
    private String photo;
    private Set<String> roles = new HashSet<>();
}

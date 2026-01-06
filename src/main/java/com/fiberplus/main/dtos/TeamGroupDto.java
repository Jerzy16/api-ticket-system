package com.fiberplus.main.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamGroupDto {
    private String role;
    private List<TeamMemberDto> members;
}

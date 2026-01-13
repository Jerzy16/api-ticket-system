package com.fiberplus.main.dtos.report;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableBoard {
    private String boardId;
    private String boardName;
    private Integer taskCount;
    private LocalDateTime createdAt;
}

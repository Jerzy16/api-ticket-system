package com.fiberplus.main.dtos.report;

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
public class BoardStatistics {
    private String boardId;
    private String boardName;
    private int totalTasks;
    private int completedTasks;
    private double completionRate;
}

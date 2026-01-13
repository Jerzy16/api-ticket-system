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
public class UserPerformance {
    private String userId;
    private String userName;
    private int tasksCompleted;
    private int evidencesProvided;
    private double averageCompletionTime;
}

package com.fiberplus.main.dtos.report;

import java.util.List;

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
public class ReportSummary {
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private int totalEvidences;
    private List<UserPerformance> userPerformance;
    private List<BoardStatistics> boardStatistics;
}

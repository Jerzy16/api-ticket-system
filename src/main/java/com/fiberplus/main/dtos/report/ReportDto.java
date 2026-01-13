package com.fiberplus.main.dtos.report;

import java.time.LocalDateTime;
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
public class ReportDto {
    private String reportId;
    private String reportType;
    private LocalDateTime generatedAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ReportSummary summary;
    private List<ReportTaskDetail> tasks;

    private List<AvailableUser> availableUsers;
    private List<AvailableBoard> availableBoards;
}

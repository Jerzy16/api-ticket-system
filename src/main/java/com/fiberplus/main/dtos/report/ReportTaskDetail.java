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
public class ReportTaskDetail {
    private String taskId;
    private String taskTitle;
    private String boardName;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String completedBy;
    private String completedByName;
    private String completionDescription;
    private String completionNotes;
    private List<String> evidenceUrls;
    private List<String> assignedUsers;
}

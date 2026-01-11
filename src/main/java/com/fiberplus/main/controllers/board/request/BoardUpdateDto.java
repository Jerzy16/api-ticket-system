package com.fiberplus.main.controllers.board.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BoardUpdateDto {
    private String type;

    private String taskId;
    private String boardId;
    private String fromBoardId;
    private String toBoardId;

    private Object task;
    private Object board;

    private Integer newIndex;
    
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
}

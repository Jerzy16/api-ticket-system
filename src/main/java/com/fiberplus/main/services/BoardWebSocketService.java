package com.fiberplus.main.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fiberplus.main.controllers.board.request.BoardUpdateDto;


@Service
public class BoardWebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(BoardWebSocketService.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    
    private static final String BOARD_UPDATES_TOPIC = "/topic/board-updates";

    public BoardWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyTaskMoved(String taskId, String fromBoardId, String toBoardId, 
                                Integer newIndex, Object task) {
        BoardUpdateDto update = BoardUpdateDto.builder()
                .type("TASK_MOVED")
                .taskId(taskId)
                .fromBoardId(fromBoardId)
                .toBoardId(toBoardId)
                .newIndex(newIndex)
                .task(task)
                .build();
        
        sendBoardUpdate(update);
        logger.info("📤 Tarea movida notificada: {} de {} a {}", taskId, fromBoardId, toBoardId);
    }

    public void notifyTaskCreated(String boardId, Object task) {
        BoardUpdateDto update = BoardUpdateDto.builder()
                .type("TASK_CREATED")
                .boardId(boardId)
                .task(task)
                .build();
        
        sendBoardUpdate(update);
        logger.info("📤 Tarea creada notificada en board: {}", boardId);
    }

    public void notifyTaskUpdated(String taskId, String boardId, Object task) {
        BoardUpdateDto update = BoardUpdateDto.builder()
                .type("TASK_UPDATED")
                .taskId(taskId)
                .boardId(boardId)
                .task(task)
                .build();
        
        sendBoardUpdate(update);
        logger.info("📤 Tarea actualizada notificada: {}", taskId);
    }

    public void notifyTaskDeleted(String taskId, String boardId) {
        BoardUpdateDto update = BoardUpdateDto.builder()
                .type("TASK_DELETED")
                .taskId(taskId)
                .boardId(boardId)
                .build();
        
        sendBoardUpdate(update);
        logger.info("📤 Tarea eliminada notificada: {}", taskId);
    }

    public void notifyBoardCreated(Object board) {
        BoardUpdateDto update = BoardUpdateDto.builder()
                .type("BOARD_CREATED")
                .board(board)
                .build();
        
        sendBoardUpdate(update);
        logger.info("📤 Board creado notificado");
    }

    private void sendBoardUpdate(BoardUpdateDto update) {
        try {
            messagingTemplate.convertAndSend(BOARD_UPDATES_TOPIC, update);
            logger.debug("✅ Mensaje enviado a {}: {}", BOARD_UPDATES_TOPIC, update.getType());
        } catch (Exception e) {
            logger.error("❌ Error al enviar actualización de board: {}", e.getMessage());
        }
    }
}
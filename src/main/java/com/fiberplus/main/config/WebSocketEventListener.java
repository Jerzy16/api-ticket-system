package com.fiberplus.main.config;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final ConcurrentHashMap<String, String> activeSessions = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anónimo";
        
        activeSessions.put(sessionId, userId);
        
        System.out.println(" Conexión WebSocket: " + userId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Total conexiones activas: " + activeSessions.size());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = activeSessions.remove(sessionId);
        
        System.out.println("❌ Desconexión WebSocket: " + userId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Total conexiones activas: " + activeSessions.size());
    }

    public int getActiveConnectionsCount() {
        return activeSessions.size();
    }

    public ConcurrentHashMap<String, String> getActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }
}
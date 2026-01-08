package com.fiberplus.main.config;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fiberplus.main.util.JwtUtil;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);
                
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                try {
                    String userId = jwtUtil.extractUserId(token);
                    
                    if (userId != null && !jwtUtil.isTokenExpired(token)) {
                        Authentication authentication = 
                            new UsernamePasswordAuthenticationToken(userId, null, null);
                        
                        accessor.setUser(authentication);
                        
                        System.out.println("✅ Usuario autenticado en WebSocket: " + userId);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error al autenticar WebSocket: " + e.getMessage());
                }
            }
        }

        return message;
    }
}